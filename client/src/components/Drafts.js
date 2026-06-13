import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { useParams } from 'react-router-dom'
import Loading from './Loading'
import authHeader from '../services/authHeader'
import { toastError } from '../utils/notification'
const Drafts = () => {
  const { userId } = useParams()
  const [draftsList, setDraftsList] = useState([])
  const [isLoading, setIsLoading] = useState(false)

  const handleDraftError = (error) => {
    let errorMessage = 'Network Down'
    if (error?.response?.status) {
      switch (error.response.status) {
        case 404:
          errorMessage = 'No Data Found 404'
          break
        case 401:
          errorMessage = 'Unauthenticated 401'
          break
        case 403:
          errorMessage = 'Unauthorized 403'
          break
        case 500:
          errorMessage = 'Server error, please try again later. ! 500'
          break
        default:
          errorMessage = `An unexpected error occurred. ${error.response.status}`
      }
    }
    toastError(errorMessage)
  }

  useEffect(() => {
    async function fetchDrafts(userId) {
      setIsLoading(true)
      try {
        const response = await axios.get(
          `http://localhost/expense/drafts/${userId}`,
          { headers: authHeader() }
        )
        setDraftsList(response.data)
        console.log('DRAFTS ---> ', response.data)
      } catch (error) {
        console.log(error)
        handleDraftError(error)
      } finally {
        setIsLoading(false) // End loading
      }
    }

    fetchDrafts(userId)
  }, [])

  if (isLoading) {
    return (
      <div className='container '>
        <Loading />
      </div>
    )
  }
  return (
    <div className='container '>
      <div>
        <h2 className='m-4'>Drafts of User with Id: {userId}</h2>
        <div class='row'></div>
        <table class='table table-striped m-4 p-3'>
          <thead>
            <tr>
              <th>Expense ID</th>
              <th>Category</th>
              <th>Expense Date</th>
              {/* <th>Receipt</th> */}
              <th>Expense</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {draftsList.map((expense) => (
              <tr>
                <td>{expense.id}</td>
                <td>{expense.category}</td>
                <td>{expense.expenseDate}</td>

                <td>
                  <a
                    href={'/expense-report/user/' + userId + '/' + expense.id}
                    class='btn btn-primary'
                  >
                    View Expense
                  </a>
                </td>

                <td>{expense.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default Drafts
