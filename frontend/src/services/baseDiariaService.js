import axios from 'axios'

const API = 'http://localhost:8080/api/base-diaria'

function getAuthHeader() {
  const token = localStorage.getItem("token")

  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  }
}

export default {

  registrarBase(data) {
    return axios.post(
      API,
      data,
      getAuthHeader()
    )
  },

  obtenerBaseDelDia() {
    return axios.get(
      `${API}/hoy`,
      getAuthHeader()
    )
  }

}