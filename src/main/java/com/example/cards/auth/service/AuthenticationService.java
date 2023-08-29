package com.example.cards.auth.service;

import com.example.cards.auth.dto.AuthenticationRequest;
import com.example.cards.auth.dto.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse signin(AuthenticationRequest request);
}
