import React from 'react'
import { useParams } from 'react-router-dom'
import { useState } from 'react'
import axios from 'axios'
import { useEffect } from 'react'
import authHeader from '../services/authHeader'
import Loading from './Loading'
import ExpenseDoghnutChart from './ExpenseDoghnutChart'
import ExpenseAmountBar from './ExpenseAmountBar'
import { toastError } from '../utils/notification'

export default function ManagerDashboard() {
  const { mgrId } = useParams()

  const [pending, setPending] = useState([])
  const [approved, setApproved] = useState([])
  const [rejected, setRejected] = useState([])
  const [approvedAmmount, setApprovedAmount] = useState(0)
  const [numPending, setNumPending] = useState(0)
  const [numApproved, setNumApproved] = useState(0)
  const [numRejected, setNumRejected] = useState(0)
  const [isLoading, setIsLoading] = useState(true)
  const [pendingAmount, setPendingAmount] = useState(0)

  const handleManagerDashboardErrors = (error) => {
    let errorMessage = 'Network Down'
    if (error?.response?.status) {
      switch (error.response.status) {
        case 404:
          errorMessage = 'No Data Found 404'
          break
        case 401:
          errorMessage = 'Not Authorized / Not Manager 401'
          break
        case 400:
          errorMessage = 'Not Authorized / Not Manager 401'
          window.location.href = '/login'
          break
        case 403:
          errorMessage = 'Not Authenticated 403'
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
    const fetchData = async () => {
      try {
        const response = await axios.get(
          `http://localhost/expense/manager/${mgrId}/all-expenses`,
          {
            headers: authHeader(),
          }
        )

        const allExpenses = response.data
        console.log(allExpenses)

        if (allExpenses === undefined) {
          return
        }

        const pendingExpenses = allExpenses.filter(
          (expense) => expense.status === 'PENDING'
        )
        const approvedExpenses = allExpenses.filter(
          (expense) => expense.status === 'APPROVED'
        )
        const rejectedExpenses = allExpenses.filter(
          (expense) => expense.status === 'REJECTED'
        )

        const totalApprovedAmount = approvedExpenses.reduce(
          (sum, expense) => sum + expense.amount,
          0
        )
        const totalPendingAmount = pendingExpenses.reduce(
          (sum, expense) => sum + expense.amount,
          0
        )
        const numPendingExpenses = pendingExpenses.length
        const numRejectedExpenses = rejectedExpenses.length
        const numApprovedExpenses = approvedExpenses.length

        setPending(pendingExpenses)
        setApproved(approvedExpenses)
        setRejected(rejectedExpenses)

        setApprovedAmount(totalApprovedAmount)
        setPendingAmount(totalPendingAmount)
        setNumPending(numPendingExpenses)
        setNumApproved(numApprovedExpenses)
        setNumRejected(numRejectedExpenses)
      } catch (error) {
        console.error('Error fetching data:', error)
        // toastError(error.message, error.response.status)
        handleManagerDashboardErrors(error)

        //   setError(error.message);
      } finally {
        setIsLoading(false)
      }
    }

    fetchData()
  }, [])

  if (isLoading) {
    return <Loading />
  }

  return (
    <div className='container mb-5'>
      <div class='row align-items-md-center mb-5'>
        <div class='col-lg-12'>
          <div class='lc-block py-2'>
            <span class='badge bg-dark' editable='inline'>
              Hyderabad
            </span>
          </div>
          <div class='lc-block mb-5'>
            <h2 editable='inline'>Review Your Team Expenses</h2>
          </div>
          <div class='lc-block mt-5'>
            <div class='row'>
              <div class='col-6'>
                <div class='lc-block mb-4'>
                  <p
                    editable='inline'
                    style={{ fontSize: '40px' }}
                    class=' m-0'
                  >
                    Rs. {approvedAmmount}
                  </p>
                  <p editable='inline' class='small text-uppercase'>
                    Total Approved
                  </p>
                </div>
              </div>
              <div class='col-6'>
                <div class='lc-block mb-4'>
                  <p
                    editable='inline'
                    class=' m-0'
                    style={{ fontSize: '40px' }}
                  >
                    {numPending}
                  </p>
                  <p editable='inline' class='small text-uppercase'>
                    Pending Expenses
                  </p>
                </div>
              </div>
            </div>
            <div class='row'>
              <div class='col-6'>
                <div class='lc-block mb-4'>
                  <p
                    editable='inline'
                    class=' m-0'
                    style={{ fontSize: '40px' }}
                  >
                    {numRejected}
                  </p>
                  <p editable='inline' class='small text-uppercase'>
                    Rejected Expenses
                  </p>
                </div>
              </div>
              <div class='col-6'>
                <div class='lc-block mb-4'>
                  <p
                    editable='inline'
                    class=' m-0'
                    style={{ fontSize: '40px' }}
                  >
                    {numApproved}
                  </p>
                  <p editable='inline' class='small text-uppercase'>
                    Approved Expenses
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class='row'>
        <div class='col-lg-6'>
          <div
            class='card shadow-lg p-3'
            style={{ maxHeight: '284px', alignItems: 'center' }}
          >
            <ExpenseAmountBar
              approvedAmount={approvedAmmount}
              pendingAmount={pendingAmount}
            />
          </div>
        </div>
        <div class='col-lg-6'>
          <div
            class='card shadow-lg p-3'
            style={{ maxHeight: '284px', alignItems: 'center' }}
          >
            <ExpenseDoghnutChart
              approved={numApproved}
              rejected={numRejected}
            />
          </div>
        </div>
      </div>

      <div class='accordion my-5' id='accordionExample'>
        <div class='accordion-item'>
          <h2 class='accordion-header' id='headingOne'>
            <button
              class='accordion-button'
              type='button'
              data-bs-toggle='collapse'
              data-bs-target='#collapseOne'
              aria-expanded='true'
              aria-controls='collapseOne'
            >
              Awaiting Approval
            </button>
          </h2>
          <div
            id='collapseOne'
            class='accordion-collapse collapse show'
            aria-labelledby='headingOne'
            data-bs-parent='#accordionExample'
          >
            <div class='accordion-body'>
              <div class='table-responsive'>
                <table class='table'>
                  <thead>
                    <tr>
                      <th scope='col'>Recipt ID</th>
                      <th scope='col'>Category</th>
                      <th scope='col'>Amount</th>
                      <th scope='col'>Details</th>
                    </tr>
                  </thead>
                  <tbody>
                    {pending
                      ? pending.map((expense) => (
                          <tr class=''>
                            <td scope='row'>{expense.id}</td>
                            <td>{expense.category}</td>
                            <td>{expense.amount}</td>
                            <td>
                              <a
                                name=''
                                id=''
                                class='btn btn-primary'
                                href={`/expense-report-mgr/exp-id/${expense.id}`}
                                role='button'
                              >
                                View
                              </a>
                            </td>
                          </tr>
                        ))
                      : ''}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class='accordion accordion-flush my-5' id='accordionFlushExample'>
        <div class='accordion-item'>
          <h2 class='accordion-header' id='flush-headingOne'>
            <button
              class='accordion-button collapsed bg-success '
              type='button'
              data-bs-toggle='collapse'
              data-bs-target='#flush-collapseOne'
              aria-expanded='true'
              aria-controls='flush-collapseOne'
            >
              Approved Expenses
            </button>
          </h2>
          <div
            id='flush-collapseOne'
            class='accordion-collapse collapse'
            aria-labelledby='flush-headingOne'
            data-bs-parent='#accordionFlushExample'
          >
            <div class='accordion-body'>
              <div class='table-responsive'>
                <table class='table'>
                  <thead>
                    <tr>
                      <th scope='col'>Recipt ID</th>
                      <th scope='col'>Category</th>
                      <th scope='col'>Amount</th>
                      <th scope='col'>Details</th>
                    </tr>
                  </thead>
                  <tbody>
                    {approved != null
                      ? approved.map((expense) => (
                          <tr class=''>
                            <td scope='row'>{expense.id}</td>
                            <td>{expense.category}</td>
                            <td>{expense.amount}</td>
                            <td>
                              <a
                                name=''
                                id=''
                                class='btn btn-primary'
                                href={`/expense-report-mgr/exp-id/${expense.id}`}
                                role='button'
                              >
                                View
                              </a>
                            </td>
                          </tr>
                        ))
                      : ''}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class='accordion accordion-flush my-5' id='accordionFlushPending'>
        <div class='accordion-item'>
          <h2 class='accordion-header' id='flush-headingTwo'>
            <button
              class='accordion-button collapsed bg-danger'
              type='button'
              data-bs-toggle='collapse'
              data-bs-target='#flush-collapseTwo'
              aria-expanded='false'
              aria-controls='flush-collapseTwo'
            >
              Rejected Expenses
            </button>
          </h2>
          <div
            id='flush-collapseTwo'
            class='accordion-collapse collapse'
            aria-labelledby='flush-headingTwo'
            data-bs-parent='#accordionFlushPending'
          >
            <div class='accordion-body'>
              <div class='table-responsive'>
                <table class='table'>
                  <thead>
                    <tr>
                      <th scope='col'>Recipt ID</th>
                      <th scope='col'>Category</th>
                      <th scope='col'>Amount</th>
                      <th scope='col'>Details</th>
                    </tr>
                  </thead>
                  <tbody>
                    {rejected != null
                      ? rejected.map((expense) => (
                          <tr class=''>
                            <td scope='row'>{expense.id}</td>
                            <td>{expense.category}</td>
                            <td>{expense.amount}</td>
                            <td>
                              <a
                                name=''
                                id=''
                                class='btn btn-primary'
                                href={`/expense-report-mgr/exp-id/${expense.id}`}
                                role='button'
                              >
                                View
                              </a>
                            </td>
                          </tr>
                        ))
                      : ''}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
