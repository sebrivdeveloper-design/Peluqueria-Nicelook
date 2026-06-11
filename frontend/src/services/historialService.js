import axios from "axios"

const API_URL = "http://localhost:8080/api/historial"

export const getHistorial = async (idCliente) => {

    return await axios.get(`${API_URL}/${idCliente}`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    })
}

export const cancelarCita = async (idCita) => {
    return await axios.put(`http://localhost:8080/api/citas/${idCita}/cancelar`, {}, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    })
}