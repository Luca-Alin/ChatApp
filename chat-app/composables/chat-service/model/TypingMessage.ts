import type { User } from "~/composables/authentication-service/model/User";

export interface TypingMessage {
    sender: User;
    receiver: User;
}