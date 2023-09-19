package com.jinseong.discordbot;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.security.auth.login.LoginException;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot extends ListenerAdapter {

	private final Logger log = LoggerFactory.getLogger(Bot.class);
	private KafkaProducer<String, String> producer;
	private KafkaConsumer<String, String> consumer;
	private TextChannel discordChannel; // Discord 채널 설정

	/* Kafka Producer & Cosumer 세팅 */
	public Bot() {

		Properties producerProperties = new Properties();
		producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		producer = new KafkaProducer<>(producerProperties);

		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group");
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
		consumer = new KafkaConsumer<>(consumerProperties);
		consumer.subscribe(Collections.singletonList("reservation-result-topic"));

	}

	public static void main(String[] args) throws LoginException, IOException, InterruptedException {

	    ExecutorService executorService = Executors.newCachedThreadPool();
		
		/* Bot Token 가져오기 */
		Properties properties = new Properties();

        InputStream inputStream = Bot.class.getClassLoader().getResourceAsStream("config.properties");
		
		properties.load(inputStream);
		inputStream.close();
		String token = properties.getProperty("bot.token");
		
		/* JdaBuilder 생성 */
		JDABuilder jdaBuilder = JDABuilder.createDefault(token).enableIntents(GatewayIntent.MESSAGE_CONTENT);
		jdaBuilder.addEventListeners(new Bot());
		
		try {
			Bot bot = new Bot();
			bot.start();
			JDA jda = jdaBuilder.build();
		    jda.awaitReady();
			
			TextChannel channel;
		    channel = jda.getTextChannelById("1138736573521870891");
	        bot.setTextChannel(channel);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*  Kafka Counsumer Thread 생성 */
	public void start() throws InterruptedException {
		Thread kafkaConsumerThread = new Thread(this::consumeKafkaMessages);
		kafkaConsumerThread.start();
	}
	
	/* 디스코드 채널 설정 */
	public void setTextChannel(TextChannel channel){
		log.info("Channel found ----" + channel.getId());
		discordChannel = channel;
	}
	

	/* 사용자 로그인 */
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		TextChannel defaultChannel = (TextChannel) event.getGuild().getDefaultChannel();
		if (defaultChannel != null) 
			defaultChannel.sendMessage("안녕하세요 " + event.getMember().getAsMention() + "님!\n !도움말\n 을 통해 명령어를 확인하세요.").queue();
	}

	/* onMessageReceived - 메세지 명령어 처리 !예약 !도움말 */
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return;
		
		MessageChannel channel = event.getChannel();
		User user = event.getAuthor();
		String content = event.getMessage().getContentRaw();

		/* 명령어 처리 */
		if (content.startsWith("!예약")) { // [!예약] 명령어 처리
			log.info("=== 예약 ===");
			String[] commandArgs = content.split(" ");

			if (commandArgs.length == 6) {
				String name = commandArgs[1];
				String date = commandArgs[2];
				String time = commandArgs[3];
				String count = commandArgs[4];
				String number = commandArgs[5];

				JSONObject reservationJson = new JSONObject();
				reservationJson.put("name", name);
				reservationJson.put("date", date);
				reservationJson.put("time", time);
				reservationJson.put("count", count);
				reservationJson.put("number", number);

				String jsonStr = reservationJson.toString();

				if (!sendReservationToKafka(jsonStr)) {channel.sendMessage("예약 요청을 전송하는 중에 문제가 발생했습니다.").queue();}
			} else {
				channel.sendMessage("유효하지 않은 명령어 형태입니다. ---> !예약 이름 날짜 시간 인원 연락처").queue();
			}
		} else if (content.equals("!도움말")) { // [!도움말] 명령어 처리
			log.debug("=== 도움말 ===");
			channel.sendMessage("[명령어]\n" + "[예약생성] - !예약 이름 날짜 시간 인원 연락처\n" + "\t ex) !예약 정진성 2023/08/09 18:00 4 01012345678").queue();
		}
	}

	/* Kafka Producer - 예약 정보 전송 (reservation-topic) */
	private boolean sendReservationToKafka(String jsonStr) {
		String topic = "reservation-topic"; 

		ProducerRecord<String, String> record = new ProducerRecord<>(topic, jsonStr);

		try {
			producer.send(record).get(); // 레코드 전송 및 완료 대기
			log.info("메세지가 전송 성공 - " + topic);
			return true;
		} catch (Exception e) {
			log.error("메세지가 전송 실패 -" + topic + ": " + e.getMessage());
			return false;
		}
	}

	/* Kafka Consumer - 예약 정보 전송 (reservation-success-topic, reservation-error-topic) */
	private void consumeKafkaMessages() {
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

			for (ConsumerRecord<String, String> r : records) {
				String message = r.value();
				log.info("예약 정보 전송 결과 : " + message);
				sendResponseToDiscord(message);
			}
		}
	}

	/* 예약 처리 결과 디스코드 메세지 전송 */
	private void sendResponseToDiscord(String message) {
		if (discordChannel != null) {
			log.info("메세지 전달 -- " + message);
			discordChannel.sendMessage("예약 처리 결과 : " + message).queue();
		}
	}


}
