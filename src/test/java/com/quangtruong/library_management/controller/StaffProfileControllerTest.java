package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.staff.StaffProfileRequest;
import com.quangtruong.library_management.dto.staff.StaffProfileResponse;
import com.quangtruong.library_management.entity.Gender;
import com.quangtruong.library_management.entity.StaffProfile;
import com.quangtruong.library_management.mapper.IStaffProfileMapper;
import com.quangtruong.library_management.service.IStaffProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StaffProfileControllerTest {

	MockMvc mockMvc;

	@Mock
	IStaffProfileService staffProfileService;

	@Mock
	IStaffProfileMapper staffProfileMapper;

	@InjectMocks
	StaffProfileController staffProfileController;

	ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(staffProfileController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	private StaffProfileRequest createValidRequest() {
		return StaffProfileRequest.builder()
				.name("Staff A")
				.email("staffa@gmail.com")
				.password("password123")
				.employeeCode("EMP001")
				.phone("0123456789")
				.gender(Gender.MALE)
				.dob(LocalDate.of(1990, 1, 1))
				.statusId(1L)
				.position(new StaffProfileRequest.StaffPositionIdRequest(1L))
				.address("Hanoi")
				.build();
	}

	@Test
	void getAll_Success() throws Exception {
		StaffProfile staff = StaffProfile.builder().id(UUID.randomUUID()).build();
		StaffProfileResponse response = StaffProfileResponse.builder().userId(UUID.randomUUID()).name("Staff A").build();

		when(staffProfileService.getAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(staff)));
		when(staffProfileMapper.toResponse(staff)).thenReturn(response);

		mockMvc.perform(get("/api/staff"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].name").value("Staff A"));
	}

	@Test
	void create_Success() throws Exception {
		StaffProfile staff = StaffProfile.builder().id(UUID.randomUUID()).build();
		StaffProfileResponse response = StaffProfileResponse.builder().userId(UUID.randomUUID()).name("Staff A").build();

		when(staffProfileService.createStaff(any())).thenReturn(staff);
		when(staffProfileMapper.toResponse(staff)).thenReturn(response);

		String requestJson = "{\n" +
				"  \"name\": \"Staff A\",\n" +
				"  \"email\": \"staffa@gmail.com\",\n" +
				"  \"password\": \"password123\",\n" +
				"  \"employeeCode\": \"EMP001\",\n" +
				"  \"phone\": \"0123456789\",\n" +
				"  \"gender\": \"MALE\",\n" +
				"  \"dob\": \"1990-01-01\",\n" +
				"  \"statusId\": 1,\n" +
				"  \"position\": { \"id\": 1 },\n" +
				"  \"address\": \"Hanoi\"\n" +
				"}";

		mockMvc.perform(post("/api/staff")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.name").value("Staff A"));
	}

	@Test
	void update_Success() throws Exception {
		UUID id = UUID.randomUUID();
		StaffProfile staff = StaffProfile.builder().id(id).build();
		StaffProfileResponse response = StaffProfileResponse.builder().userId(id).name("Staff Updated").build();

		when(staffProfileService.updateStaff(any(UUID.class), any())).thenReturn(staff);
		when(staffProfileMapper.toResponse(staff)).thenReturn(response);

		String requestJson = "{\n" +
				"  \"name\": \"Staff Updated\",\n" +
				"  \"email\": \"staffa@gmail.com\",\n" +
				"  \"password\": \"password123\",\n" +
				"  \"employeeCode\": \"EMP001\",\n" +
				"  \"phone\": \"0123456789\",\n" +
				"  \"gender\": \"MALE\",\n" +
				"  \"dob\": \"1990-01-01\",\n" +
				"  \"statusId\": 1,\n" +
				"  \"position\": { \"id\": 1 },\n" +
				"  \"address\": \"Hanoi\"\n" +
				"}";

		mockMvc.perform(put("/api/staff/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("Staff Updated"));
	}

	@Test
	void delete_Success() throws Exception {
		UUID id = UUID.randomUUID();
		mockMvc.perform(delete("/api/staff/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Deleted staff successfully"));
	}
}
