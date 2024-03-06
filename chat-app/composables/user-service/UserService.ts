import axios from "axios";
import { env } from "~/env";
import type { User } from "../authentication-service/model/User";
import type { UserAndMessage } from "../authentication-service/model/UserAndMessage";

class UserService {
    url =  `${env.baseApiUrl}/user`;

    findAllUsers() {
        const user = JSON.parse(sessionStorage.getItem("user")!);
        return axios.post<UserAndMessage[]>(this.url, user);
    } 
}

const userService = new UserService();
export default userService;