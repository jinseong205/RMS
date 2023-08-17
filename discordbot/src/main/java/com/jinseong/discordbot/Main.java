package com.jinseong.discordbot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main extends ListenerAdapter {

	private final Logger log = LoggerFactory.getLogger(Main.class);

	/**
	 * Main
	 */
	public static void main(String[] args) throws LoginException, IOException {
		
		Properties properties = new Properties();
		InputStream inputStream = null;

		try {
			inputStream = Main.class.getClassLoader().getResourceAsStream("config.properties");
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
		
		String token = properties.getProperty("bot.token");
		
		JDABuilder.createDefault(token).enableIntents(GatewayIntent.MESSAGE_CONTENT) // Add this line
				.addEventListeners(new Main()).build();
	}

	/**
	 * onGuildMemberJoin - 사용자 로그인
	 */
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		TextChannel defaultChannel = (TextChannel) event.getGuild().getDefaultChannel();

		if (defaultChannel != null) {
			defaultChannel.sendMessage("안녕하세요 " + event.getMember().getAsMention() + "님!\n !도움말\n 을 통해 명령어를 확인하세요.")
					.queue();
		}
	}

	/**
	 * onMessageReceived - 메세지 명령어 처리 !예약 !도움말
	 */
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot())
			return;
		MessageChannel channel = event.getChannel();
		User user = event.getAuthor();
		String content = event.getMessage().getContentRaw();

		if (content.startsWith("!예약")) { // [!예약] 명령어 처리

			log.info("=== 예약 ===");
			String[] commandArgs = content.split(" ");
			
			if (commandArgs.length == 6) {
				String name = commandArgs[1];
				String date = commandArgs[2];
				String time = commandArgs[3];
				String count = commandArgs[4];
				String number = commandArgs[5];
				String response = "예약 전송" + "-> 이름: " + name + " / 날짜: " + date + " / 시간: " + time + " / 인원: " + count + "/ 연락처:" + number;
				channel.sendMessage(response).queue();
				sendReservationToKafka(name, date, time, count, number);
			} else {
				channel.sendMessage("유효하지 않은 명령어 형태입니다. ---> !예약 이름 날짜 시간 인원 연락처").queue();
			}

		} else if (content.equals("!도움말")) { // [!도움말] 명령어 처리
			log.debug("=== 도움말 ===");
			channel.sendMessage("[명령어] \n" + "예약 생성 ---> !예약 이름 날짜 시간 인원 연락처 \n" + "\t ex) !예약 정진성 2023/08/09 18:00 4 01012345678")
					.queue();
		}
	}

	private void sendReservationToKafka(String name, String date, String time, String count, String number) {

		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka 브로커 주소
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

		String topic = "reservation-topic"; // 예약 정보를 보낼 Kafka 토픽 이름

		String message = "Name: " + name + ", Date: " + date + ", Time: " + time + ", Count: " + count + ",Number : " + number;
		ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);

		producer.send(record, (metadata, exception) -> {
			if (exception == null) {
				log.info("메세지가 전송 성공 - " + topic);
			} else {
				log.info("메세지가 전송 실패 -" + topic + ": " + exception.getMessage());
			}
		});

		producer.close();
	}

}
