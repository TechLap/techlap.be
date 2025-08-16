package com.example.techlap.service.impl;

import com.example.techlap.domain.User;
import com.example.techlap.exception.ResourceAlreadyExistsException;
import com.example.techlap.repository.UserRepository;
import com.example.techlap.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(User user) throws Exception {
        // Check Username
        if (this.userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceAlreadyExistsException(user.getEmail() + " đã tồn tại");
        }

        // Save hashPassword
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Đoạn code xử lí việc thêm role nhưng hiện giờ thì set = null trước
        user.setRole(null);
//        Role role = this.roleRepository.findById(user.getRole().getId()).orElse(null);
//        if (role != null) {
//            user.setRole(role);
//        }else {
//            user.setRole(null);
//        }

        return userRepository.save(user);
    }

    @Override
    public User update(User user) throws Exception {
        User userInDB = this.userRepository.findById(user.getId()).orElse(null);
        // Check Username
        if (userInDB == null) {
            throw new ResourceAlreadyExistsException(user.getId() + " không tồn tại");
        }
        userInDB.setFullName(user.getFullName());
        userInDB.setPhone(user.getPhone());

        return this.userRepository.save(userInDB);
    }

    @Override
    public User fetchUserById(long id) throws Exception {
        if (!this.userRepository.existsById(id)) {
            throw new ResourceAlreadyExistsException("người dùng không tồn tại");
        }
        return this.userRepository.findById(id).orElse(null);
    }

    @Override
    public User fetchUserByEmail(String email) {
        if (!this.userRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException("người dùng không tồn tại");
        }
        return this.userRepository.findByEmail(email);
    }

    @Override
    public void delete(long id) throws Exception {
        if (!this.userRepository.existsById(id)) {
            throw new ResourceAlreadyExistsException("người dùng không tồn tại");
        }
        this.userRepository.deleteById(id);
    }
}
