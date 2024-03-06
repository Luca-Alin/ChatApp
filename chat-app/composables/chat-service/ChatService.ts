import {useUsersStore} from "~/store/UsersStore";
import type {MessageTransferObject} from "./model/MessageTransferObject";
import type {User} from "../authentication-service/model/User";
import type {TypingMessage} from "./model/TypingMessage";
import type {Message} from "./model/Message";
import {createPinia} from "pinia";

export class ChatService {
    private static instance: null | ChatService = null;
    public ws: WebSocket | null = null;
    private wsUrl: string = "ws://localhost:8080/chat";
    private usersStore = useUsersStore();

    private constructor() {
        const pinia = createPinia();
    }

    static getInstance() {
        if (ChatService.instance == null)
            ChatService.instance = new ChatService();
        return ChatService.instance;
    }

    connectWebSocket(): void {
        if (this.ws != null) {
            return;
        }

        console.log("Connecting web socket...");

        const username = sessionStorage.getItem("username");
        if (username == null) {
            console.log("Username is null and the websocket can't connect");
            return;
        }


        const connectionUrl = `${this.wsUrl}?username=${username}`;
        this.ws = new WebSocket(connectionUrl);

        this.setWsMethods();
        console.log("WebSocket connected");
    }

    disconnectWebSocket(): void {
        console.log("disconnecting web socket");
        this.ws?.close();
    }

    private setWsMethods() {
        if (this.ws == null)
            return;

        this.ws.onclose = () => {
            this.ws = null;
            console.log(this.ws);
        };

        this.ws.onmessage = (message) => {
            const parsedMessage: MessageTransferObject = JSON.parse(message.data);
            console.log(parsedMessage);
            switch (parsedMessage.type) {
                case "connect":
                    const connectedUser = parsedMessage.content as User;
                    this.usersStore.setUserOnline(connectedUser);
                    break;
                case "disconnect":
                    const disconnectedUser = parsedMessage.content as User;
                    this.usersStore.setUserOffline(disconnectedUser);
                    console.log(this.usersStore.getUsers());
                    break;
                case "typingmessage":
                    const typingMessage = parsedMessage.content as TypingMessage;
                    this.usersStore.setUserTyping(typingMessage.sender);
                    break;
                case "sendmessage":
                    const message = parsedMessage.content as Message;
                    this.usersStore.receiveMessage(message);
                    break;
                default:
                    console.error(`no mappping for this kind of message ${parsedMessage.type}`);
            }

        };
    };
}
