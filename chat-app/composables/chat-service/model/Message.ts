import type {User} from "~/composables/authentication-service/model/User";

export interface Message {
    sender: User;
    receiver: User;
    textMessage: string;
}