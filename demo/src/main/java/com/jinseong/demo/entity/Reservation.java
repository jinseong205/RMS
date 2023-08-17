package com.jinseong.demo.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table
@Data
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // project에 연결된 db의 넘버링 전략을 따라간다.
	@Column(name="reservation_id")
	private Long id;
	
	private String name;
	
	private String date;
	
	private String time;
	
	private String count;
	
	private String number;
}
