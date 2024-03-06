import type { Message } from "~/composables/chat-service/model/Message";
import type { User } from "./User";

export interface UserAndMessage {
    user: User;
    messages: Message[];
}