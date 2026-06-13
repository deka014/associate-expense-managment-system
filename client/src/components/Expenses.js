import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { useParams } from 'react-router-dom'
import Loading from './Loading'
import authHeader from '../services/authHeader'
import { toastError } from '../utils/notification'

const Expenses = () => {
  const [expensesList, setExpensesList] = useState([])
  const { id, category, status } = useParams()
  const [startDate, setStartDate] = useState(null)
  const [endDate, setEndDate] = useState(null)
  const [isLoading, setIsLoading] = useState(true)

  console.log(id, category, status)

  const handleExpenseError = (error) => {
    let errorMessage = 'Network Down'
    if (error?.response?.status) {
      switch (error.response.status) {
        case 404:
          errorMessage = 'No Data Found 404'
          break
        case 403:
          errorMessage = 'Un-Authenticated 403'
          break
        case 500:
          errorMessage = 'Server error, please try again later. ! 500'
          break
        default:
          // errorMessage = `Unhandelled Exception. ${error.response.status}`
          window.location.href = "/login"
          return
      }
    }
    toastError(errorMessage)
  }

  let userId = id

  const handleStartDateChange = (event) => {
    setStartDate(event.target.value)
  }

  const handleEndDateChange = (event) => {
    setEndDate(event.target.value)
  }

  useEffect(() => {
    async function fetchExpenseList(id) {
      try {
        const response = await axios.get(
          `http://localhost/expense/${id}`,
          { headers: authHeader() }
        )
        setExpensesList(response.data)
        console.log('EXPENSES BY ID ---> ', response.data)
      } catch (error) {
        handleExpenseError(error)
      } finally {
        setIsLoading(false) // End loading
      }
    }

    async function fetchExpenseListByIdAndCategory(id, category) {
      try {
        const response = await axios.get(
          `http://localhost/expense/${id}/${category}`,
          { headers: authHeader() }
        )
        setExpensesList(response.data)
        console.log('EXPENSES BY ID AND CATEGORY---> ', response.data)
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
          handleExpenseError(error)
        }
      } finally {
        setIsLoading(false) // End loading
      }
    }

    async function fetchExpenseListByIdAndStatus(id, status) {
      try {
        const response = await axios.get(
          `http://localhost/expense/${id}/status/${status}`,
          { headers: authHeader() }
        )
        setExpensesList(response.data)
        console.log('EXPENSES BY ID AND STATUS---> ', response.data)
      } catch (error) {
        console.error('Error fetching expenses data:', error)
        handleExpenseError(error)
      } finally {
        setIsLoading(false) // End loading
      }
    }

    async function fetchExpenseListByIdAndDates(id) {
      try {
        const response = await axios.get(
          `http://localhost/expense/find/${id}?startDate=${startDate}&endDate=${endDate}`,
          { headers: authHeader() }
        )
        setExpensesList(response.data)
        console.log('EXPENSES BY ID AND DATES---> ', response.data)
      } catch (error) {
        console.error('Error fetching expenses data:', error)
        handleExpenseError(error)
      } finally {
        setIsLoading(false) // End loading
      }
    }

    async function fetchExpenseListByStatusAndDates(
      id,
      status,
      startDate,
      endDate
    ) {
      try {
        const response = await axios.get(
          `http://localhost/expense/${id}/status/${status}/dates?startDate=${startDate}&endDate=${endDate}`,
          { headers: authHeader() }
        )
        setExpensesList(response.data)
        console.log('EXPENSES BY ID AND DATES---> ', response.data)
      } catch (error) {
        console.error('Error fetching expenses data:', error)
        handleExpenseError(error)
      } finally {
        setIsLoading(false) // End loading
      }
    }

    if (id && status && startDate && endDate) {
      fetchExpenseListByStatusAndDates(
        id,
        status.toUpperCase(),
        startDate,
        endDate
      )
    } else if (id && category) {
      fetchExpenseListByIdAndCategory(id, category.toUpperCase())
    } else if (id && status) {
      fetchExpenseListByIdAndStatus(id, status.toUpperCase())
    } else if (id && startDate && endDate) {
      fetchExpenseListByIdAndDates(id)
    } else if (id) {
      fetchExpenseList(id)
    }
  }, [id, category, status, startDate, endDate])

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
        <h2 className='m-4'>Expenses of User with Id: {id}</h2>
        <div class='row'>
          <div class='col-6'>
            <div class='input-group mb-3'>
              <span class='input-group-text'>From</span>
              <input
                type='date'
                className='form-control'
                id='fromDate'
                value={startDate}
                onChange={handleStartDateChange}
              />
            </div>
          </div>
          <div class='col-6'>
            <div class='input-group mb-3'>
              <span class='input-group-text'>To</span>
              <input
                type='date'
                className='form-control'
                id='toDate'
                value={endDate}
                onChange={handleEndDateChange}
              />
            </div>
          </div>
        </div>
        <table class='table table-striped my-4 p-3'>
          <thead>
            <tr>
              <th>Expense ID</th>
              <th>Category</th>
              <th>Expense Date</th>
              <th>Status</th>
              <th>Submission Status</th>
              <th>Expense</th>
            </tr>
          </thead>
          <tbody>
            {expensesList.map((expense) => (
              <tr>
                <td>{expense.id}</td>
                <td>{expense.category}</td>
                <td>{expense.expenseDate}</td>

                <td>{expense.status}</td>
                <td>
                  {expense.submitStatus === 'TRUE'
                    ? 'SUBMITTED'
                    : 'NOT SUBMITTED'}
                </td>

                <td>
                  <a
                    href={'/expense-report/user/' + userId + '/' + expense.id}
                    class='btn btn-primary btn'
                  >
                    View Expense
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

export default Expenses
