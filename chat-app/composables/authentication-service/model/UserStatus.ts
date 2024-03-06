import type { Message } from "~/composables/chat-service/model/Message";
import type { User } from "./User";

export interface UserStatus {
    user: User;
    connectionStatus: "Online" | "Offline";
    /**
     * The websocket will send typing notification, and when those are received,
     * the unix time will be stored inside this field.
     * One way to implement typing notifications is to chech whether 5s have passed
     * since the last typing notificatin was sent
     */
    isTyping: number;
    messages: Message[];
}