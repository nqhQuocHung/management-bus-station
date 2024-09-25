package com.nqh.bus_station_management.bus_station.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CargoDTO {

    @NotNull
    @Size(max = 255, message = "Receiver name must be at most 255 characters.")
    private String receiverName;

    @NotNull
    @Size(max = 50, message = "Receiver email must be at most 50 characters.")
    private String receiverEmail;

    @NotNull
    @Size(max = 20, message = "Receiver phone must be at most 20 characters.")
    private String receiverPhone;

    @NotNull
    @Size(max = 255, message = "Receiver address must be at most 255 characters.")
    private String receiverAddress;

    @NotNull
    @Min(value = 0, message = "Cargo price must be greater than or equal to 0.")
    private Double cargoPrice;

    @NotNull(message = "Description cannot be null.")
    private String description;

    @NotNull(message = "Ticket ID cannot be null.")
    private Long ticketId;
}
