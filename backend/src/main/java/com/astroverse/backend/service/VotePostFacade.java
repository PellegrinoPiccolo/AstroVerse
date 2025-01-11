package com.astroverse.backend.service;

import com.astroverse.backend.component.JwtUtil;
import com.astroverse.backend.model.Post;
import com.astroverse.backend.model.User;
import com.astroverse.backend.model.Vote;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class VotePostFacade {
    private final UserService userService;
    private final PostService postService;
    private final VoteService voteService;

    public VotePostFacade(UserService userService, PostService postService, VoteService voteService) {
        this.userService = userService;
        this.postService = postService;
        this.voteService = voteService;
    }

    public String votePost(long id, boolean vote, String token) {
        token = token.replace("Bearer ", "");
        DecodedJWT decodedJWT = JwtUtil.JwtDecode(token);
        String email = decodedJWT.getClaim("email").asString();
        User user = userService.getUser(email);
        Post post = postService.getPost(id);
        Optional<Vote> optionalVote = voteService.existVote(user.getId(), post.getId());
        if (optionalVote.isPresent()) {
            Vote oldVote = optionalVote.get();
            if (oldVote.isVote() != vote) {
                voteService.updateVote(oldVote.getId(), vote);
                return "Voto aggiornato";
            } else {
                voteService.deleteVote(oldVote.getId());
                return "Voto eliminato";
            }
        }
        Vote newVote = new Vote(post, user, vote);
        Vote savedVote = voteService.saveVote(newVote);
        System.out.println(savedVote.getId());
        return "Votazione al post effettuata con successo";
    }
}