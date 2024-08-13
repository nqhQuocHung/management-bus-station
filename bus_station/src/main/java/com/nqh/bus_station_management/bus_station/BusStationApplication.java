package com.nqh.bus_station_management.bus_station;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.awt.*;
import java.net.URI;

@SpringBootApplication
public class BusStationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusStationApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void openBrowser() {
		try {
			System.out.println("Kiểm tra hỗ trợ Desktop...");
			if (Desktop.isDesktopSupported()) {
				System.out.println("Desktop được hỗ trợ. Đang mở trình duyệt...");
				Desktop.getDesktop().browse(new URI("http://localhost:8080"));
				System.out.println("Trình duyệt đã được mở.");
			} else {
				System.out.println("Desktop API không được hỗ trợ.");
			}
		} catch (Exception e) {
			System.out.println("Có lỗi khi mở trình duyệt:");
			e.printStackTrace();
		}
	}

}
