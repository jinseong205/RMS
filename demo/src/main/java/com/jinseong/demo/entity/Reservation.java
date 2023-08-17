package com.jinseong.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	@Column(name="reservation_id")
	private Long id;
	
	private String name;
	
	private String date;
	
	private String time;
	
	private String count;
	
	private String number;

	private String status;
	@Override
	public String toString() {
		return "id=" + id + ", name=" + name + ", date=" + date + ", time=" + time + ", count=" + count
				+ ", number=" + number;
	}
	
	
}
