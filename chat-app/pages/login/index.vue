<script setup lang="ts">
import loginService from "~/composables/authentication-service/AuthenticationService";
import type {LoginRequest} from "~/composables/authentication-service/model/LoginRequest";
import {ChatService} from "~/composables/chat-service/ChatService";

const chatService = ChatService.getInstance();

const loginIsInvalid = ref(false);

const email = ref("");
const password = ref("");
const passwordVisible = ref(false);

const login = async () => {
  const loginRequest: LoginRequest = {
    email: email.value,
    password: password.value
  };

  await loginService.login(loginRequest)
      .then((res) => {
        sessionStorage.setItem("username", res.data.email);
        sessionStorage.setItem("user", JSON.stringify(res.data));
        chatService.connectWebSocket();

        navigateTo("/chat");
      })
      .catch(err => {
        console.log(err);
        loginIsInvalid.value = true;
        setTimeout(() => {
          loginIsInvalid.value = false;
        }, 5000);
      })
      .finally(() => {
      });
};
</script>

<template>
  <div class="d-flex flex-column justify-center align-center">
    <div v-if="loginIsInvalid">
      <v-alert closable type="error" title="Invalid login" variant="tonal">
      </v-alert>
    </div>


    <v-sheet width="300" class="mx-auto">
      <form @submit.prevent="login">
        <v-text-field type="email" v-model="email" label="email"></v-text-field>

        <v-text-field :append-inner-icon="passwordVisible ? 'mdi-eye-off' : 'mdi-eye'"
                      :type="passwordVisible ? 'text' : 'password'"
                      placeholder="Enter your password" prepend-inner-icon="mdi-lock-outline"
                      @click:append-inner="passwordVisible = !passwordVisible" v-model="password"></v-text-field>
        <v-btn type="btn" block class="mt-2">Log in</v-btn>
      </form>
    </v-sheet>

  </div>
</template>

<style scoped></style>