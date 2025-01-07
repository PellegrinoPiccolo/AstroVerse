import axios from "axios";
import ServerUrl from "@/constants/ServerUrl.js";
import Cookies from "js-cookie";

export const apiUrlToken = axios.create({
    baseURL: `${ServerUrl}/api`,
})

export const apiUrl = axios.create({
    baseURL: `${ServerUrl}/api`
})

export const apiTokenJson = axios.create({
    baseURL: `${ServerUrl}/api`,
    headers: {
        'Content-Type': 'application/json'
    }
})

export const apiJson = axios.create({
    baseURL: `${ServerUrl}/api`,
    headers: {
        'Content-Type': 'application/json'
    }
})

export const apiForm = axios.create({
    baseURL: `${ServerUrl}/api`,
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
    }
})

export const apiTokenForm = axios.create({
    baseURL: `${ServerUrl}/api`,
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
    }
})

const addTokenInterceptor = (axiosIstance) => {
        axiosIstance.interceptors.request.use(
            (config) => {
                const accessToken = Cookies.get("accessToken")
                if(accessToken) {
                    config.headers['Authorization'] = `Bearer ${accessToken}`
                }
                return config
            },
            (error) => {
                return Promise.reject(error)
            })
}

addTokenInterceptor(apiTokenJson)
addTokenInterceptor(apiTokenForm)
addTokenInterceptor(apiUrlToken)