import axios from "axios";
import ServerUrl from "@/constants/ServerUrl.js";
import Cookies from "js-cookie";

const accessToken = Cookies.get('accessToken') || '';

export const apiUrlToken = axios.create({
    baseURL: `${ServerUrl}/api`,
    headers: {
        'Authorization': `Bearer ${accessToken}`
    }
})

export const apiUrl = axios.create({
    baseURL: `${ServerUrl}/api`
})

export const apiTokenJson = axios.create({
    baseURL: `${ServerUrl}/api`,
    headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
    }
})

export const ApiJson = axios.create({
    baseURL: `${ServerUrl}/api`,
    headers: {
        'Content-Type': 'application/json'
    }
})