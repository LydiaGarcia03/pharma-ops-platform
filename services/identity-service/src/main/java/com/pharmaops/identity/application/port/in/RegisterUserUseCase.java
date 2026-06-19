package com.pharmaops.identity.application.port.in;

import com.pharmaops.identity.domain.model.User;

public interface RegisterUserUseCase {

    User registerUser(Command command);

    record Command(String name, String email, String password) {}
}
