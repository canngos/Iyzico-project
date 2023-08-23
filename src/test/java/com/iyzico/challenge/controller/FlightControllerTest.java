package com.iyzico.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyzico.challenge.dto.FlightDto;
import com.iyzico.challenge.dto.SeatDto;
import com.iyzico.challenge.exception.BusinessException;
import com.iyzico.challenge.exception.Status;
import com.iyzico.challenge.exception.TransactionCode;
import com.iyzico.challenge.request.FlightRequest;
import com.iyzico.challenge.request.SeatRequest;
import com.iyzico.challenge.response.*;
import com.iyzico.challenge.service.DefaultFlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlightController.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DefaultFlightService defaultFlightService;

    @Autowired
    private ObjectMapper objectMapper;

    private DefaultMessageResponse defaultMessageResponse;
    private Long flightId;
    private Long seatId;
    private FlightRequest flightRequest;

    @BeforeEach
    void setUp() {
        flightId = 1L;
        seatId = 1L;
        defaultMessageResponse = new DefaultMessageResponse();
        defaultMessageResponse.setBody(new BaseBody<>(new DefaultMessageBody("success")));
        defaultMessageResponse.setStatus(new Status(TransactionCode.SUCCESS));
        flightRequest = new FlightRequest();
        flightRequest.setFlightName("test");
        flightRequest.setOrigin("test");
        flightRequest.setDestination("test");
        flightRequest.setDepartureTime("22-04-2023 14:30");
        flightRequest.setArrivalTime("22-04-2023 14:30");
        flightRequest.setPrice(BigDecimal.valueOf(1000));
    }

    @Test
    void testCreateFlight_whenProvidedFlightAlreadyExists_shouldReturnBadRequest() throws Exception {
        BusinessException ex = new BusinessException(TransactionCode.FLIGHT_ALREADY_EXISTS);

        Mockito.when(defaultFlightService.createFlight(Mockito.any(FlightRequest.class))).thenThrow(ex);

        var request = MockMvcRequestBuilders.post("/flight/create")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(flightRequest))
                .accept("application/json");
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = response.getResponse().getContentAsString();
        DefaultMessageResponse responseObj = objectMapper.readValue(content, DefaultMessageResponse.class);

        assertEquals(400, response.getResponse().getStatus());
        assertEquals("101", responseObj.getStatus().getCode());
    }

    @Test
    void testCreateFlight_whenRequestValid_shouldReturnIdAndSuccess() throws Exception {
        FlightResponse flightResponse = new FlightResponse();
        FlightResponseBody body = new FlightResponseBody();
        body.setFlightId(1L);
        body.setMessage("success");
        flightResponse.setBody(new BaseBody<>(body));
        flightResponse.setStatus(new Status(TransactionCode.SUCCESS));

        Mockito.when(defaultFlightService.createFlight(Mockito.any(FlightRequest.class))).thenReturn(flightResponse);

        var request = MockMvcRequestBuilders.post("/flight/create")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(flightRequest))
                .accept("application/json");
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String content = response.getResponse().getContentAsString();
        FlightResponse responseObj = objectMapper.readValue(content, FlightResponse.class);

        assertEquals(200, response.getResponse().getStatus());
        assertEquals(1L, responseObj.getBody().getData().getFlightId());
    }

    @Test
    void testDeleteFlight_whenRequestValid_shouldReturnAppropriateMessage() throws Exception {
        Mockito.when(defaultFlightService.deleteFlight(Mockito.anyLong())).thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.delete("/flight/" + flightId)
                .contentType("application/json")
                .accept("application/json");

        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testUpdateFlight_whenRequestAndIdValid_shouldReturnAppropriateMessage() throws Exception {
        Mockito.when(defaultFlightService.updateFlight(Mockito.anyLong(), Mockito.any(FlightRequest.class))).thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.put("/flight/" + flightId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(flightRequest))
                .accept("application/json");

        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testAddSeat_whenRequestAndFlightIdValid_shouldReturnSeatIdAndSuccess() throws Exception {
        SeatResponse seatResponse = new SeatResponse();
        SeatResponseBody body = new SeatResponseBody();
        body.setSeatId(1L);
        body.setMessage("success");
        seatResponse.setBody(new BaseBody<>(body));
        seatResponse.setStatus(new Status(TransactionCode.SUCCESS));
        SeatRequest seatRequest = new SeatRequest();
        seatRequest.setSeatName("test");

        Mockito.when(defaultFlightService.addSeat(Mockito.anyLong(), Mockito.any(SeatRequest.class))).thenReturn(seatResponse);

        var request = MockMvcRequestBuilders.post("/flight/add/seat/" + flightId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(seatRequest))
                .accept("application/json");

        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String content = response.getResponse().getContentAsString();
        SeatResponse responseObj = objectMapper.readValue(content, SeatResponse.class);

        assertEquals(200, response.getResponse().getStatus());
        assertEquals(1L, responseObj.getBody().getData().getSeatId());
    }

    @Test
    void testDeleteSeat_whenIdsValid_shouldReturnAppropriateMessage() throws Exception {
        Mockito.when(defaultFlightService.deleteSeat(Mockito.anyLong(), Mockito.anyLong())).thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.delete("/flight/" + flightId + "/seat/" + seatId)
                .contentType("application/json")
                .accept("application/json");

        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testUpdateSeat_whenRequestAndIdsValid_shouldReturnAppropriateMessage() throws Exception {
        SeatRequest seatRequest = new SeatRequest();
        seatRequest.setSeatName("test");

        Mockito.when(defaultFlightService.updateSeat(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(SeatRequest.class))).thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.put("/flight/" + flightId + "/seat/" + seatId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(seatRequest))
                .accept("application/json");

        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testGetAllFlights_whenRequested_shouldReturnListOfFlightsWithAvailableSeats() throws Exception {
        DetailFlightResponse detailFlightResponse = new DetailFlightResponse();
        DetailFlightResponseBody body = new DetailFlightResponseBody();
        List<FlightDto> flightDtoList = new ArrayList<>();
        FlightDto flightDto = new FlightDto();
        flightDto.setFlightName("test");
        flightDto.setOrigin("test");
        flightDto.setDestination("test");
        flightDto.setDepartureTime(LocalDateTime.now());
        flightDto.setArrivalTime(LocalDateTime.now());
        flightDto.setPrice(BigDecimal.valueOf(1000));
        List<SeatDto> seatDtoList = new ArrayList<>();
        SeatDto seatDto = new SeatDto();
        seatDto.setSeatName("test");
        seatDtoList.add(seatDto);
        flightDto.setAvaliableSeats(seatDtoList);
        flightDtoList.add(flightDto);
        body.setFlightList(flightDtoList);

        detailFlightResponse.setBody(new BaseBody<>(body));
        detailFlightResponse.setStatus(new Status(TransactionCode.SUCCESS));

        Mockito.when(defaultFlightService.getAllFlights()).thenReturn(detailFlightResponse);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/flight/all")
                .contentType("application/json")
                .accept("application/json");


        MvcResult response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String content = response.getResponse().getContentAsString();
        DetailFlightResponse responseObj = objectMapper.readValue(content, DetailFlightResponse.class);

        assertEquals(200, response.getResponse().getStatus());
        assertEquals(1, responseObj.getBody().getData().getFlightList().size());
        assertEquals(1, responseObj.getBody().getData().getFlightList().get(0).getAvaliableSeats().size());
    }

    @Test
    void testBookSeat_whenIdsValid_shouldReturnAppropriateMessage() throws Exception {
        Mockito.when(defaultFlightService.bookSeat(Mockito.anyLong(), Mockito.anyLong())).thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/flight/" + flightId + "/book/" + seatId)
                .contentType("application/json")
                .accept("application/json");

        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }


}
