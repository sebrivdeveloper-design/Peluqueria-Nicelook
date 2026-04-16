import axios from 'axios'

const API = 'http://localhost:8080/api/empleados'

function getAuthHeader() {
  const token = localStorage.getItem("token")

  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  }
}

export const crearEmpleado = (data) =>
  axios.post(API, data, getAuthHeader())

export const getEmpleados = () =>
  axios.get(API, getAuthHeader())
