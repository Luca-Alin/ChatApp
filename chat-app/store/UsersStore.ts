import {defineStore} from "pinia";
import type {Ref} from "vue";
import type {User} from "~/composables/authentication-service/model/User";
import type {UserAndMessage} from "~/composables/authentication-service/model/UserAndMessage";
import type {UserStatus} from "~/composables/authentication-service/model/UserStatus";
import type {Message} from "~/composables/chat-service/model/Message";


export const useUsersStore = defineStore("usersStore", () => {

    const users: Ref<UserStatus[]> = ref([]);

    function getUsers(): Ref<UserStatus[]> {
        return users;
    }

    function addUser(userAndMessage: UserAndMessage) {
        const userStatus: UserStatus = {
            user: userAndMessage.user,
            connectionStatus: "Offline",
            isTyping: 0,
            messages: userAndMessage.messages
        };
        users.value.push(userStatus);
    }

    function setUserOnline(user: User) {
        console.log(`setting user `);

        users.value
            .filter(u => u.user.email == user.email)[0]
            .connectionStatus = "Online";

        console.log("Updated users: " + users.value[0])
    }

    function setUserOffline(user: User) {
        users.value
            .filter(u => u.user.email == user.email)[0]
            .connectionStatus = "Offline";
    }

    function setUserTyping(user: User) {
        users.value
            .filter(u => u.user.email == user.email)[0]
            .isTyping = new Date().getTime();
    }

    function receiveMessage(message: Message) {
        users.value
            .filter(u => u.user.email == message.sender.email || u.user.email == message.receiver.email)[0]
            .messages
            .push(message);
    }

    function deleteUsers() {
        users.value = [];
    }

    return {users, getUsers, deleteUsers, addUser, setUserOnline, setUserOffline, receiveMessage, setUserTyping};
});