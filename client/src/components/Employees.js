import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { useParams } from 'react-router-dom'
import Loading from './Loading'
import authHeader from '../services/authHeader'
import { toastError } from '../utils/notification'

const Employees = () => {
  const [employeesList, setEmployeesList] = useState([])
  const { id, mngrId } = useParams()
  const [isLoading, setIsLoading] = useState(false)

  useEffect(() => {
    console.log('testing')
    async function fetchAllEmployees() {
      setIsLoading(true)
      try {
        const response = await axios.get(`http://localhost/user `, {
          headers: authHeader(),
        })
        console.log(response, 'this is the response')
        setEmployeesList(response.data)
        console.log('EMPLOYEES ---> ', response.data)
      } catch (error) {
        console.error('Error fetching expenses data:', error)

        toastError(error.message)
      } finally {
        setIsLoading(false) // End loading
      }
    }

    async function fetchEmployeeById(id) {
      setIsLoading(true)
      try {
        const response = await axios.get(`http://localhost/user/${id}`, {
          headers: authHeader(),
        })
        setEmployeesList([response.data])
        console.log('EMPLOYEE BY ID ---> ', response.data)
      } catch (error) {
        console.error('Error fetching expenses data:', error)

        toastError(error.message)
      } finally {
        setIsLoading(false) // End loading
      }
    }

    if (id) {
      fetchEmployeeById(id)
    } else {
      fetchAllEmployees()
    }
  }, [id, mngrId])

  if (isLoading) {
    return (
      <div className='container '>
        <Loading />
      </div>
    )
  }

  return (
    <div className='container mb-5 '>
      <div>
        <h2 className='mb-5'>Employees</h2>

        <table class='table table-striped m-4 p-3'>
          <thead>
            <tr>
              <th>Employee ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Department</th>
              <th>Role</th>
              <th>Manager Id</th>
              <th>Expenses</th>
            </tr>
          </thead>
          <tbody>
            {employeesList.map((employee) => (
              <tr>
                <td>{employee.id}</td>
                <td>{employee.name}</td>
                <td>{employee.email}</td>
                <td>{employee.department}</td>
                <td>{employee.role}</td>
                <td>
                  {employee.role === 'EMPLOYEE' ? employee.managerId : 'N/A'}
                </td>
                <td>
                  <a
                    href={'/expense/' + employee.id}
                    class='btn btn-primary btn'
                  >
                    View Expenses
                  </a>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default Employees
