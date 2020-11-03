/**
 * Copyright (c) 2020, Self XDSD Contributors
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
package com.selfxdsd.selfweb.api.output;

import com.selfxdsd.api.Project;
import com.selfxdsd.api.ProjectManager;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import com.selfxdsd.api.Wallet;
import java.math.BigDecimal;
import javax.json.Json;

import javax.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.hamcrest.Matchers;
import org.hamcrest.MatcherAssert;



/**
 * Unit tests for {@link JsonProject}.
 * @author Lumi
 * @todo #152:60min Continue writing unit tests for the json output classes.
 *  We can start with output class JsonContract.
 */
public final class JsonProjectTestCase {
    

    /**
     * JsonProject has repoFullName.
     */    
    @Test
    public void hasRepoFullName(){
        final Project project = Mockito.mock(Project.class);
        
        Mockito.when(project.repoFullName()).thenReturn("Andreea/Test");
        Mockito.when(project.provider()).thenReturn("Github");
        
        User owner = Mockito.mock(User.class);        
        
        Mockito.when(owner.username()).thenReturn("Andreea");        
        
        Mockito.when(project.owner()).thenReturn(owner);
        
        ProjectManager manager = Mockito.mock(ProjectManager.class);    
        
        Mockito.when(manager.id()).thenReturn(123);
        Mockito.when(manager.userId()).thenReturn("321");
        Mockito.when(manager.username()).thenReturn("charlsemike");
        
        Provider provider = Mockito.mock(Provider.class);
        
        Mockito.when(provider.name()).thenReturn("Github");
        
        Mockito.when(manager.provider()).thenReturn(provider);        
        Mockito.when(manager.commission()).thenReturn(BigDecimal.valueOf(800));  
        
        Mockito.when(project.projectManager()).thenReturn(manager);
        
        Wallet wallet = Mockito.mock(Wallet.class);
        
        Mockito.when(wallet.type()).thenReturn("Stripe");
        Mockito.when(wallet.active()).thenReturn(true);
        Mockito.when(wallet.cash()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(wallet.debt()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(wallet.available()).thenReturn(BigDecimal.valueOf(800));
        
        Mockito.when(project.wallet()).thenReturn(wallet);       
        
        final JsonObject jsonProject = new JsonProject(project);
        
        MatcherAssert.assertThat(
            jsonProject.getString("repoFullName"),
            Matchers.equalTo("Andreea/Test")
        );   
    }
    /**
     * JsonProject has Provider.
     */   
    @Test
    public void hasProvider(){
    
        final Project project = Mockito.mock(Project.class);
        
        Mockito.when(project.repoFullName()).thenReturn("Andreea/Test");
        Mockito.when(project.provider()).thenReturn("Github");
        
        User owner = Mockito.mock(User.class);
        Mockito.when(owner.username()).thenReturn("Andreea");
        
        Mockito.when(project.owner()).thenReturn(owner);
        
        ProjectManager manager = Mockito.mock(ProjectManager.class);
        
        Mockito.when(manager.id()).thenReturn(123);
        Mockito.when(manager.userId()).thenReturn("321");
        Mockito.when(manager.username()).thenReturn("charlsemike");
        
        Provider provider = Mockito.mock(Provider.class);
        
        Mockito.when(provider.name()).thenReturn("Github");
        
        Mockito.when(manager.provider()).thenReturn(provider);        
        Mockito.when(manager.commission()).thenReturn(BigDecimal.valueOf(800));
        
        Mockito.when(project.projectManager()).thenReturn(manager);
        
        Wallet wallet = Mockito.mock(Wallet.class);
        
        Mockito.when(wallet.type()).thenReturn("Stripe");
        Mockito.when(wallet.active()).thenReturn(true);
        Mockito.when(wallet.cash()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(wallet.debt()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(wallet.available()).thenReturn(BigDecimal.valueOf(800));
        
        Mockito.when(project.wallet()).thenReturn(wallet);       
        
        final JsonObject jsonProject = new JsonProject(project);
        
        MatcherAssert.assertThat(
            jsonProject.getString("provider"),
            Matchers.equalTo("Github")
        );       
    }
    
    /**
     * JsonProject has Owner.
     */
    @Test
    public void hasOwner(){
    
        final Project project = Mockito.mock(Project.class);
        
        Mockito.when(project.repoFullName()).thenReturn("Andreea/Test");
        Mockito.when(project.provider()).thenReturn("Github");
        
        User owner = Mockito.mock(User.class);
        Mockito.when(owner.username()).thenReturn("Andreea");
        
        Mockito.when(project.owner()).thenReturn(owner);
        
        ProjectManager manager = Mockito.mock(ProjectManager.class);
        
        Mockito.when(manager.id()).thenReturn(123);
        Mockito.when(manager.userId()).thenReturn("321");
        Mockito.when(manager.username()).thenReturn("charlsemike");
        
        Provider provider = Mockito.mock(Provider.class);
        
        Mockito.when(provider.name()).thenReturn("Github");
        
        Mockito.when(manager.provider()).thenReturn(provider);        
        Mockito.when(manager.commission()).thenReturn(BigDecimal.valueOf(800));
        
        Mockito.when(project.projectManager()).thenReturn(manager);
        
        Wallet wallet = Mockito.mock(Wallet.class);
        
        Mockito.when(wallet.type()).thenReturn("Stripe");
        Mockito.when(wallet.active()).thenReturn(true);
        Mockito.when(wallet.cash()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(wallet.debt()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(wallet.available()).thenReturn(BigDecimal.valueOf(800));
        
        Mockito.when(project.wallet()).thenReturn(wallet);       
        
        final JsonObject jsonProject = new JsonProject(project);
        
        MatcherAssert.assertThat(
            jsonProject.getString("selfOwner"),
            Matchers.equalTo("Andreea")
        );            
        
    }
    
    /**
     * JsonProject has Manager.
     */
    @Test
    public void hasManager(){
    
        final Project project = Mockito.mock(Project.class);
        
        Mockito.when(project.repoFullName()).thenReturn("Andreea/Test");
        Mockito.when(project.provider()).thenReturn("Github");
        
        User owner = Mockito.mock(User.class);
        Mockito.when(owner.username()).thenReturn("Andreea");
        
        Mockito.when(project.owner()).thenReturn(owner);
        
        ProjectManager manager = Mockito.mock(ProjectManager.class);
        
        Mockito.when(manager.id()).thenReturn(123);
        Mockito.when(manager.userId()).thenReturn("321");
        Mockito.when(manager.username()).thenReturn("charlsemike");
        
        Provider provider = Mockito.mock(Provider.class);
        
        Mockito.when(provider.name()).thenReturn("Github");
        
        Mockito.when(manager.provider()).thenReturn(provider);        
        Mockito.when(manager.commission()).thenReturn(BigDecimal.valueOf(800));
        
        Mockito.when(project.projectManager()).thenReturn(manager);
        
        Wallet wallet = Mockito.mock(Wallet.class);
        
        Mockito.when(wallet.type()).thenReturn("Stripe");
        Mockito.when(wallet.active()).thenReturn(true);
        Mockito.when(wallet.cash()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(wallet.debt()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(wallet.available()).thenReturn(BigDecimal.valueOf(800));
        
        Mockito.when(project.wallet()).thenReturn(wallet);       
        
        final JsonObject jsonProject = new JsonProject(project);
        
        MatcherAssert.assertThat(
            jsonProject.getJsonObject("manager"),
            Matchers.equalTo(Json.createObjectBuilder()
                .add("id", manager.id())
                .add("userId", manager.userId())
                .add("username", manager.username())
                .add("provider", manager.provider().name())
                .add(
                    "commission",
                    manager
                        .commission()
                        .divide(BigDecimal.valueOf(100))
                        .doubleValue()
                )
                .build())
        );       
        
    }
    
    /**
     * JsonProject has Wallet.
     */
    @Test
    public void hasWallet(){
    
        final Project project = Mockito.mock(Project.class);
        
        Mockito.when(project.repoFullName()).thenReturn("Andreea/Test");
        Mockito.when(project.provider()).thenReturn("Github");
        
        User owner = Mockito.mock(User.class);
        Mockito.when(owner.username()).thenReturn("Andreea");
        
        Mockito.when(project.owner()).thenReturn(owner);
        
        ProjectManager manager = Mockito.mock(ProjectManager.class);
        
        Mockito.when(manager.id()).thenReturn(123);
        Mockito.when(manager.userId()).thenReturn("321");
        Mockito.when(manager.username()).thenReturn("charlsemike");
        
        Provider provider = Mockito.mock(Provider.class);
        
        Mockito.when(provider.name()).thenReturn("Github");
        
        Mockito.when(manager.provider()).thenReturn(provider);        
        Mockito.when(manager.commission()).thenReturn(BigDecimal.valueOf(800));
        
        Mockito.when(project.projectManager()).thenReturn(manager);
        
        Wallet wallet = Mockito.mock(Wallet.class);
        
        Mockito.when(wallet.type()).thenReturn("Stripe");
        Mockito.when(wallet.active()).thenReturn(true);
        Mockito.when(wallet.cash()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(wallet.debt()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(wallet.available()).thenReturn(BigDecimal.valueOf(800));
        
        Mockito.when(project.wallet()).thenReturn(wallet);       
        
        final JsonObject jsonProject = new JsonProject(project);
        
        MatcherAssert.assertThat(
            jsonProject.getJsonObject("wallet"),
            Matchers.equalTo(Json.createObjectBuilder()
                .add("type", wallet.type())
                .add("active", wallet.active())
                .add("cash", wallet.cash().divide(BigDecimal.valueOf(100)))
                .add("debt", wallet.debt().divide(BigDecimal.valueOf(100)))
                .add(
                    "available",
                    wallet.available().divide(BigDecimal.valueOf(100))
                ).build())
        );       
    }
   
}
