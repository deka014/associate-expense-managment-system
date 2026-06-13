import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { useParams } from 'react-router-dom'
import Loading from './Loading'
import authHeader from '../services/authHeader'


const EmployeesOfManager = () => {

    const [employeesList, setEmployeesList] = useState([])
    const { mngrId } = useParams()
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        async function fetchEmployeesOfManager(mngrId) {
            setIsLoading(true);
            try {
                const response = await axios.get(`http://localhost/user/manager/${mngrId}`,
                { headers: authHeader() })
                setEmployeesList(response.data)
                console.log("EMPLOYEES UNDER MANAGER ---> ", response.data)
            } catch (error) {
                if (error.response && error.response.status === 403) {
                    // Handle 403 Unauthorized error
                    window.location.href = '/'
                }
                // You can display a message to the user or redirect them to the login page
                else if (error.response && error.response.status === 401) {
                    // alert('Not Authenticated ')
                    window.location.href = '/login'
                } else {

                    console.error('Error fetching expenses data:', error)
                }
            } finally {
                setIsLoading(false); // End loading
            }
        }

        fetchEmployeesOfManager(mngrId)

    }, [mngrId])

    if (isLoading) {
        return <div className="container ">
            <Loading />
        </div>
    }


    return <div className="container ">
        <div>
            <h2 className='m-4'>Employees under Manager ID: {mngrId} </h2>

            <table class="table table-striped m-4 p-3">
                <thead>
                    <tr>
                        <th>Employee ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Department</th>
                        <th>Role</th>
                        <th>Manager Id</th>

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
                            <td>{employee.role === 'EMPLOYEE' ?
                                employee.managerId :
                                'N/A'
                            }</td>

                        </tr>
                    ))}
                </tbody>
            </table>


        </div>
    </div>


}

export default EmployeesOfManager
