package com.example.FinalProject.controllers;

import com.example.FinalProject.FinalProjectApplication;
import com.example.FinalProject.config.TestConfig;
import com.example.FinalProject.models.Bookmark;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.repositories.BookmarkRepository;
import com.example.FinalProject.repositories.PostRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FinalProjectApplication.class)
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class BookmarkControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private BookmarkRepository repository;
        @Autowired
        private PostRepository postRepository;
        @Autowired
        private ObjectMapper mapper;

        @BeforeEach
        void beforeEach() {
                repository.deleteAll();
                postRepository.deleteAll();
                mapper.registerModule(new JavaTimeModule());
        }

        @AfterEach
        void afterEach() {
                repository.deleteAll();
                postRepository.deleteAll();
        }

        @Test
        void testAddBookmark_whenValid_shouldReturnCreatedBookmarkAndStatus201() throws Exception {
                Post post = new Post();
                post.setId("post1");
                post.setTitle("Title1");
                post.setContent("Content1");
                post.setAuthorId("user1");
                post.setCreatedAt(LocalDateTime.now());
                postRepository.save(post);
                BookmarkRequestPayload payload = new BookmarkRequestPayload("user1", "post1");
                MvcResult result = mockMvc.perform(
                                post("/api/bookmarks")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(payload)))
                                .andExpect(status().isCreated())
                                .andReturn();

                Bookmark created = mapper.readValue(
                                result.getResponse().getContentAsString(), Bookmark.class);
                assertThat(created.getId()).isNotNull();
                assertThat(created.getUserId()).isEqualTo("user1");
                assertThat(created.getPostId()).isEqualTo("post1");
                assertThat(created.getCreatedAt()).isNotNull();
        }

        @Test
        void testAddBookmark_whenAlreadyExists_shouldReturnStatus409() throws Exception {
                Post existPost = new Post();
                existPost.setId("post2");
                existPost.setTitle("Title2");
                existPost.setContent("Content2");
                existPost.setAuthorId("user2");
                existPost.setCreatedAt(LocalDateTime.now());
                postRepository.save(existPost);
                Bookmark exist = new Bookmark("user2", "post2");
                exist.setCreatedAt(LocalDateTime.now());
                repository.save(exist);

                BookmarkRequestPayload payload = new BookmarkRequestPayload("user2", "post2");
                mockMvc.perform(
                                post("/api/bookmarks")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(payload)))
                                .andExpect(status().isConflict());
        }

        @Test
        void testGetBookmarks_byUserId_shouldReturnUserBookmarksAndStatus200() throws Exception {
                Bookmark b1 = new Bookmark("user3", "postA");
                b1.setCreatedAt(LocalDateTime.now());
                repository.save(b1);
                Bookmark b2 = new Bookmark("user3", "postB");
                b2.setCreatedAt(LocalDateTime.now());
                repository.save(b2);
                Bookmark b3 = new Bookmark("user4", "postC");
                b3.setCreatedAt(LocalDateTime.now());
                repository.save(b3);

                MvcResult result = mockMvc.perform(get("/api/bookmarks").param("userId", "user3"))
                                .andExpect(status().isOk())
                                .andReturn();

                List<Bookmark> list = mapper.readValue(
                                result.getResponse().getContentAsString(),
                                new TypeReference<List<Bookmark>>() {
                                });
                assertThat(list).hasSize(2);
                assertThat(list).extracting(Bookmark::getPostId)
                                .containsExactlyInAnyOrder("postA", "postB");
        }

        @Test
        void testGetBookmarks_byUserId_whenNoBookmarks_shouldReturnEmptyListAndStatus200() throws Exception {
                mockMvc.perform(get("/api/bookmarks").param("userId", "none"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void testDeleteBookmark_whenExists_shouldReturnStatus204() throws Exception {
                Bookmark b = new Bookmark("user5", "postToDelete");
                b.setCreatedAt(LocalDateTime.now());
                Bookmark saved = repository.save(b);

                mockMvc.perform(delete("/api/bookmarks/" + saved.getId()))
                                .andExpect(status().isNoContent());

                mockMvc.perform(get("/api/bookmarks").param("userId", "user5"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void testDeleteBookmark_whenNotExists_shouldReturnStatus404() throws Exception {
                mockMvc.perform(delete("/api/bookmarks/nonexistent"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testAddThenDeleteFlow_shouldAddThenDeleteBookmarkViaApi() throws Exception {
                Post apiPost = new Post();
                apiPost.setId("apiPost");
                apiPost.setTitle("ApiTitle");
                apiPost.setContent("ApiContent");
                apiPost.setAuthorId("apiUser");
                apiPost.setCreatedAt(LocalDateTime.now());
                postRepository.save(apiPost);
                BookmarkRequestPayload payload = new BookmarkRequestPayload("apiUser", "apiPost");
                MvcResult postResult = mockMvc.perform(post("/api/bookmarks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(payload)))
                                .andExpect(status().isCreated())
                                .andReturn();

                Bookmark created = mapper.readValue(
                                postResult.getResponse().getContentAsString(), Bookmark.class);

                mockMvc.perform(delete("/api/bookmarks/" + created.getId()))
                                .andExpect(status().isNoContent());

                mockMvc.perform(get("/api/bookmarks").param("userId", created.getUserId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(0));
        }
}