package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.role.RoleRequest;
import com.quangtruong.library_management.dto.role.RoleResponse;
import com.quangtruong.library_management.service.IRoleService;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleControllerTest {

	MockMvc mockMvc;

	@Mock
	IRoleService roleService;

	@InjectMocks
	RoleController roleController;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(roleController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getAll_Success() throws Exception {
		RoleResponse response = RoleResponse.builder().id(1L).name("LIBRARIAN").build();
		when(roleService.getAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(response)));

		mockMvc.perform(get("/api/roles"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].name").value("LIBRARIAN"));
	}

	@Test
	void getById_Success() throws Exception {
		RoleResponse response = RoleResponse.builder().id(1L).name("LIBRARIAN").build();
		when(roleService.getById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/roles/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("LIBRARIAN"));
	}

	@Test
	void create_Success() throws Exception {
		RoleRequest request = new RoleRequest("ADMIN", null, null);
		RoleResponse response = RoleResponse.builder().id(2L).name("ADMIN").build();

		when(roleService.create(any())).thenReturn(response);

		mockMvc.perform(post("/api/roles")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.name").value("ADMIN"));
	}

	@Test
	void update_Success() throws Exception {
		RoleRequest request = new RoleRequest("UPDATED", null, null);
		RoleResponse response = RoleResponse.builder().id(1L).name("UPDATED").build();

		when(roleService.update(anyLong(), any())).thenReturn(response);

		mockMvc.perform(put("/api/roles/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("UPDATED"));
	}

	@Test
	void delete_Success() throws Exception {
		mockMvc.perform(delete("/api/roles/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Deleted successfully"));
	}
}
