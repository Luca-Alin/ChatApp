<script setup lang="ts">

import {onMounted, type Ref} from "vue";
import type {User} from "~/composables/authentication-service/model/User";
import type {MessageTransferObject} from "~/composables/chat-service/model/MessageTransferObject";
import type {Message} from "~/composables/chat-service/model/Message";

const user: Ref<User | null> = ref(null);
let ws: WebSocket | null = null;
const messages: Ref<Message[] | null> = ref(null);

const props = defineProps({
  propUser: {
    type: Object
  },
  propMessages: {
    type: Object
  },
  propWs: {
    type: Object
  },
  triggerPrintHelloWorld: {}
});


onMounted(() => {
  user.value = props.propUser as User;
  ws = props.propWs as WebSocket;
  messages.value = props.propMessages as Message[];
  console.log(messages.value);
});

const currentMessage = ref("");

const sendMessage = () => {
  const message: Message = {
    sender: JSON.parse(sessionStorage.getItem("user")!),
    receiver: user.value!,
    textMessage: currentMessage.value
  };
  const mto: MessageTransferObject = {
    type: "sendmessage",
    content: message
  };
  ws?.send(JSON.stringify(mto));
  currentMessage.value = "";
};

watch(currentMessage, (newVal, oldVal) => {
  const typingMessage = JSON.stringify({
    type: "typingmessage", content: {
      sender: JSON.parse(sessionStorage.getItem("user")!),
      receiver: user.value!
    }
  });
  console.log(user.value);
  ws?.send(typingMessage);
});
</script>

<template>
  <div class="d-flex flex-column" style="max-height: 100%; max-width: 100%; overflow: hidden">
    <!-- First div -->
    <div class="bg-blue">
      <div class="d-flex align-center">
        <div>
          <v-btn
              @click="triggerPrintHelloWorld"
              class="ma-2"
              color="white"
              icon="mdi-arrow-left"
              variant="text"
          ></v-btn>
        </div>
        <div>
          <h1>{{ user?.firstName }} {{ user?.lastName }}</h1>
        </div>
      </div>
    </div>

    <!-- Second div with flex-grow -->
    <div style="overflow-y: scroll" class="flex-fill">
      <div class="flex-column" v-for="message of messages">

        <div class="flex-fill border d-flex rounded">
          <div v-if="message.sender.email == user?.email" class="flex-fill d-flex justify-start">
            <div class="bg-green rounded">
              {{ message.sender.firstName }}: {{ message.textMessage }}
            </div>
          </div>
          <div v-else class="flex-fill d-flex justify-end">
            <div class="bg-blue rounded">
              {{ message.sender.firstName }}: {{ message.textMessage }}
            </div>
          </div>
        </div>

      </div>
    </div>

    <!-- Third div -->
    <div class="flex-column">
      <form class="d-flex" @submit.prevent="sendMessage">
        <v-text-field v-model="currentMessage" type="text" label="Message"></v-text-field>
        <v-btn type="btn">Send Message</v-btn>
      </form>
    </div>


  </div>

</template>

<style scoped></style>