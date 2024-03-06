<script setup lang="ts">
import { onMounted, type Ref } from "vue";
import type { UserStatus } from "~/composables/authentication-service/model/UserStatus";

const props = defineProps({
  propUser: {
    type: Object
  }
});

const userStatus: Ref<UserStatus | null> = ref(null);

onMounted(() => {
  userStatus.value = props.propUser as UserStatus;
});

const currentTime = ref(new Date().getTime());

const typingString = ref("•");

setInterval(() => {
  currentTime.value = new Date().getTime();
  if (typingString.value.length > 5)
    typingString.value = "•";
  else
    typingString.value += "•";
}, 1000); // Update current time every second

const isTyping = computed(() => {
  try {
    const typingTimeDifference = userStatus?.value!.isTyping! + 5000 - currentTime.value;
    return typingTimeDifference > 0;
  } catch {
    return false;
  }
});

</script>

<template>

  <div class="">
    <div>
      <v-card :title="`${userStatus?.user?.firstName} ${userStatus?.user?.lastName}`"
        :subtitle="(isTyping == true) ? typingString : userStatus?.connectionStatus" 
        variant="tonal">
      </v-card>
    </div>
  </div>

</template>

<style scoped></style>