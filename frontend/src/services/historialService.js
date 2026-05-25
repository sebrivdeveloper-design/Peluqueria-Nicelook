import axios from "axios"

const API_URL = "http://localhost:8080/api/historial"

export const getHistorial = async (idCliente) => {

    return await axios.get(`${API_URL}/${idCliente}`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    })
}