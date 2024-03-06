<script setup lang="ts">

import {useUsersStore} from "~/store/UsersStore";
import type {Ref} from "vue";
import type {User} from "~/composables/authentication-service/model/User";
import userService from "~/composables/user-service/UserService";
import {createPinia, storeToRefs} from "pinia";
import {ChatService} from "~/composables/chat-service/ChatService";
import type {Message} from "~/composables/chat-service/model/Message";
import {navigateTo} from "nuxt/app";

const pinia = createPinia();

const usersStore = useUsersStore();
const {users} = storeToRefs(usersStore);

const isLoading = ref(false);

let chatService: ChatService | null = null;

const currentUserIAmChattingWith: Ref<User | null> = ref(null);
const currentUserIAmChattingWithMessages: Ref<Message[] | null> = ref(null);
const chatWithUser = (user: User) => {

  currentUserIAmChattingWith.value = user;
  currentUserIAmChattingWithMessages.value = usersStore.getUsers()
      .value
      .filter(u => u.user.email == currentUserIAmChattingWith.value?.email)[0]
      .messages;

};

onMounted(() => {
  const user = sessionStorage.getItem("user");
  if (user == null) {
    navigateTo("/login");
  }


  isLoading.value = true;


  usersStore.deleteUsers();
  userService.findAllUsers()
      .then((res) => {
        console.log(res);
        res.data.forEach(user => usersStore.addUser(user));

        //!!! Connect the websocket only after fetching all users from the backend
        chatService = ChatService.getInstance();
        chatService.connectWebSocket();
      })
      .catch((err) => {
        console.error(err);
      })
      .finally(() => {
        isLoading.value = false;
      });

});

const printHelloWorld = () => {
  currentUserIAmChattingWith.value = null;
}

</script>

<template>
  <div class="d-flex" style="max-height: 100%; max-width: 100%">

    <!-- list of user that you can chat with -->

    <div
        v-if="$vuetify.display.xs && currentUserIAmChattingWith === null"
        class="flex-shrink-0 flex-fill"
    >
      <ul>
        <li v-for="userStatus in users">
          <div @click="currentUserIAmChattingWith = null; chatWithUser(userStatus.user)">
            <AppChatUser :prop-user="userStatus"/>
          </div>
        </li>
      </ul>
    </div>
    <div v-else-if="!$vuetify.display.xs">
      <ul>
        <li v-for="userStatus in users">
          <div @click="currentUserIAmChattingWith = null; chatWithUser(userStatus.user)">
            <AppChatUser :prop-user="userStatus"/>
          </div>
        </li>
      </ul>
    </div>


    <!-- the part of the page where you can see / send messages -->
    <div v-if="currentUserIAmChattingWith" class="d-sm-flex flex-fill">
      <div class="flex-fill" v-if="currentUserIAmChattingWith">
        <AppChatPanel :prop-user="currentUserIAmChattingWith"
                      :prop-ws="chatService?.ws"
                      :prop-messages="currentUserIAmChattingWithMessages"
                      :trigger-print-hello-world="printHelloWorld"
        />
      </div>
      <div v-else class="flex-fill">
        Select a user to chat with!
      </div>
    </div>
    <div v-else></div>

  </div>
</template>

<style scoped></style>