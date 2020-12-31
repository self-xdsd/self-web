/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.selfweb;

import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for the logged user page.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
@Controller
public class UserController {

    /**
     * Logged user.
     */
    private final User user;

    /**
     * Ctor.
     * @param user Logged user.
     */
    @Autowired
    public UserController(final User user) {
        this.user = user;
    }

    /**
     * Serve the User page of Self.
     * @param model Model that holds the user data.
     * @return User page.
     */
    @GetMapping("/user")
    public String index(final Model model) {
        final String email;
        if(user.email().isBlank()){
            email = "-";
        }else{
            email = user.email();
        }

        final String providerName = user.provider().name();
        //capitalize first letter
        final String provider = providerName.substring(0, 1).toUpperCase()
            + providerName.substring(1);
        //provider user profile links
        final String providerProfile;
        if (provider.equalsIgnoreCase(Provider.Names.GITHUB)) {
            providerProfile = "https://github.com/" + user.username();
        } else if (provider.equalsIgnoreCase(Provider.Names.GITLAB)) {
            providerProfile = "https://gitlab.com/" + user.username();
        } else {
            providerProfile = "#";
        }

        model.addAttribute("username", "@" + user.username())
            .addAttribute("email", email)
            .addAttribute("provider", provider)
            .addAttribute("providerProfile", providerProfile);
        return "user.html";
    }

}
