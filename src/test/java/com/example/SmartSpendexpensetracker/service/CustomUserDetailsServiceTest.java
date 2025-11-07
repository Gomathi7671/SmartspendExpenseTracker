package com.example.SmartSpendexpensetracker.service;

import com.example.SmartSpendexpensetracker.model.UserSpend;
import com.example.SmartSpendexpensetracker.repository.UserspendRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserspendRepository repo;

    @InjectMocks
    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_ShouldReturnUserDetails_WhenUserExistsAndEnabled() {
        UserSpend user = new UserSpend();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setEnabled(true);
        user.setRoles(Set.of("ROLE_USER")); // ✅ corrected

        when(repo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = service.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertEquals(1, userDetails.getAuthorities().size());

        verify(repo, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLoadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(repo.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("nonexistent@example.com"));

        verify(repo, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void testLoadUserByUsername_ShouldReturnDisabledUser_WhenUserNotEnabled() {
        UserSpend user = new UserSpend();
        user.setEmail("disabled@example.com");
        user.setPassword("password123");
        user.setEnabled(false);
        user.setRoles(Set.of("ROLE_USER")); // ✅ corrected

        when(repo.findByEmail("disabled@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = service.loadUserByUsername("disabled@example.com");

        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled()); // disabled user
        assertEquals(1, userDetails.getAuthorities().size());

        verify(repo, times(1)).findByEmail("disabled@example.com");
    }
}
