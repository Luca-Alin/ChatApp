<script setup lang="ts">

import type {User} from "~/composables/authentication-service/model/User";
import type {Ref} from "vue";

const user : Ref<User | null> = ref(null);

onMounted(() => {
  const userString = sessionStorage.getItem("user");
  user.value = JSON.parse(userString!);
});

</script>

<template>


  <div class="d-flex flex-column justify-center align-center">
    <div class="flex-row">
      <div v-if="user">
        <h1>Welcome {{ user.firstName }} {{ user.lastName }}</h1>
      </div>
      <div v-else>
        <h1>This is my chat application</h1>
      </div>
    </div>
    <div class="flex-row d-flex justify-space-between">
      <div>
        <router-link to="/login">Click here to login</router-link>
      </div>
      <div>
        <router-link to="/chat">Click here to chat</router-link>
      </div>
    </div>
  </div>

  

</template>

<style scoped></style>