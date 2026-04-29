package com.astroverse.backend.service;

import com.astroverse.backend.model.User;
import com.astroverse.backend.model.Space;
import com.astroverse.backend.model.UserSpace;
import com.astroverse.backend.repository.UserRepository;
import com.astroverse.backend.repository.UserSpaceRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserSpaceRepository userSpaceRepository;
    private final HybridCryptoService hybridCryptoService;

    public UserService(UserRepository userRepository, UserSpaceRepository userSpaceRepository, HybridCryptoService hybridCryptoService) {
        this.userSpaceRepository = userSpaceRepository;
        this.userRepository = userRepository;
        this.hybridCryptoService = hybridCryptoService;
    }

    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email già in uso");
        }
        // Controllo duplicati su hash
        String emailHash = hybridCryptoService.hashString(user.getEmail());
        if (userRepository.existsByEmailHash(emailHash)) {
            throw new IllegalArgumentException("Email già in uso");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username già in uso");
        }
        // Eseguiamo la cifratura Ibrida Post-Quantum
        var encryptionResult = hybridCryptoService.encrypt(user.getEmail());

        // Impostiamo i dati nel model User
        user.setEmail(encryptionResult.encryptedData()); // Salviamo il cifrato
        user.setEmailHash(emailHash); // Salviamo l'hash per le ricerche
        user.setQuantumEncapsulation(encryptionResult.encapsulation());
        user.setClientEccPublicKey(encryptionResult.clientEccPub());
        user.setQuantumEncrypted(true);
        return userRepository.save(user);
    }

    public User getUser(String email) {
        // Effettuiamo la ricerca per hash poiché non possiamo farla per email in chiaro
        String emailHash = hybridCryptoService.hashString(email);
        User user = userRepository.findByEmailHash(emailHash).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("L'utente non esiste");
        }
        // Se l'utente è cifrato, decifriamo l'email prima di restituire l'oggetto
        // così il controller riceve l'email leggibile
        if (user.isQuantumEncrypted()) {
            String decryptedEmail = hybridCryptoService.decrypt(
                    user.getEmail(),
                    user.getQuantumEncapsulation(),
                    user.getClientEccPublicKey()
            );
            user.setEmail(decryptedEmail); // Sostituiamo temporaneamente nell'oggetto
        }
        return user;
    }

    public User changeUserData(long id, String email, String username, String name, String lastName, String oldEmail, String oldUsername) {
        // Calcoliamo il nuovo hash
        String newEmailHash = hybridCryptoService.hashString(email);
        if (userRepository.existsByEmailHashAndIdNot(newEmailHash, id) && !email.equals(oldEmail)) {
            throw new IllegalArgumentException("Email già in uso");
        } else if (userRepository.existsByUsernameAndIdNot(username, id) && !username.equals(oldUsername)) {
            throw new IllegalArgumentException("Username già in uso");
        }
        // Recuperiamo l'utente
        User user = userRepository.getUserById(id);
        if (user == null) {
            throw new RuntimeException("Utente non trovato");
        }
        // Se l'email è cambiata, dobbiamo ricifrare tutto
        if (!email.equals(oldEmail)) {
            var encryptionResult = hybridCryptoService.encrypt(email);
            user.setEmail(encryptionResult.encryptedData());
            user.setEmailHash(newEmailHash);
            user.setQuantumEncapsulation(encryptionResult.encapsulation());
            user.setClientEccPublicKey(encryptionResult.clientEccPub());
            user.setQuantumEncrypted(true);
        }
        // Aggiorniamo gli altri campi
        user.setUsername(username);
        user.setNome(name);
        user.setCognome(lastName);
        return userRepository.save(user);
    }

    public void changePassword(String username, String password) {
        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Utente non esistente");
        }
        if (userRepository.updatePasswordByUsername(username, password) == 0) {
            throw new RuntimeException("Errore nella modifica della password");
        }
    }

    public User getUserData(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Utente non esistente");
        }
        // Per vedere la mail in chiaro nell'account
        User user = userRepository.getUserById(id);
        if (user.isQuantumEncrypted()) {
            user.setEmail(hybridCryptoService.decrypt(
                    user.getEmail(), user.getQuantumEncapsulation(), user.getClientEccPublicKey()
            ));
        }
        return user;
    }

    public String getOldPassword(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Utente non esistente");
        }
        return userRepository.findPasswordById(id);
    }

    public List<Space> getSpaceByUser(User user) {
        List<UserSpace> userSpaces = userSpaceRepository.findByUser(user);
        List<Space> spaces = new ArrayList<>();
        for (UserSpace userSpace : userSpaces) {
            spaces.add(userSpace.getSpace());
        }
        return spaces;
    }
}