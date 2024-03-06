import axios, {type AxiosResponse} from "axios";
import type {LoginRequest} from "~/composables/authentication-service/model/LoginRequest";
import type {User} from "~/composables/authentication-service/model/User";

class AuthenticationService {
    apiBaseUrl: string = "http://localhost:8080/api/v1/auth"
    async login(loginRequest: LoginRequest): Promise<AxiosResponse<User>>  {
        return await axios.post<User>(`${this.apiBaseUrl}/login`, loginRequest);
    }
}

const loginService = new AuthenticationService();
export default loginService;