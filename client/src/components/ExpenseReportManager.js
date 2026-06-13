import React, { useState, useEffect } from 'react'
import axios from 'axios'
import { useParams } from 'react-router-dom'
import Loading from './Loading'
import authHeader from '../services/authHeader'
import { toastError } from '../utils/notification'

const ExpenseReportManager = () => {
  const [expenseData, setExpenseData] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [receiptFileType, setReceiptFileType] = useState('')
  const [updatedComment, setUpdatedComment] = useState('')

  const { expenseId, userId } = useParams()

  const handleAddComment = (event) => {
    event.preventDefault()

    console.log('UPDATED COMMENT----> ', updatedComment)

    axios
      .put(
        `http://localhost/expense/add-comment/${expenseId}?mgrComment=${updatedComment}`,
        {},
        {
          headers: authHeader(),
        }
      )
      .then((response) => {
        console.log('Success:', response.data)
        window.location.reload()
      })
      .catch((error) => {
        console.error('Error:', error)
        // Handle error
        toastError(error.message)
      })
  }

  const [receiptData, setReceiptData] = useState({
    receiptFile: null,
  })

  const onChangeHandler = (event) => {
    event.preventDefault()
    const { value } = event.target
    setUpdatedComment(value)
  }

  const handleApproval = (event) => {
    event.preventDefault()

    axios
      .put('http://localhost/expense/updateStatus', expenseData, {
        params: {
          status: 'APPROVED', // Set the status to APPROVED
        },
        headers: authHeader(),
      })
      .then((response) => {
        console.log('Expense Approved successfully:', response.data)
        window.location.reload()
        // Handle successful response
      })
      .catch((error) => {
        console.error('Error updating expense:', error)
        toastError(error.message)
        // Handle error
      })
  }

  const handleRejection = (event) => {
    event.preventDefault()

    axios
      .put('http://localhost/expense/updateStatus', expenseData, {
        params: {
          status: 'REJECTED', // Set the status to APPROVED
        },
        headers: authHeader(),
      })
      .then((response) => {
        console.log('Expense rejected successfully:', response.data)
        window.location.reload()
      })
      .catch((error) => {
        console.error('Error updating expense:', error)
        // Handle error
        toastError(error.message)
      })
  }

  useEffect(() => {
    async function fetchExpense(expenseId) {
      try {
        const response = await axios.get(
          `http://localhost/expense/exp-id/${expenseId}`,
          { headers: authHeader() }
        )
        setExpenseData(response.data)
        console.log('EXPENSE ---> ', response.data)
      } catch (error) {
        if (error.response && error.response.status === 403) {
          // Handle 403 Unauthorized error
          // window.location.href = '/'
          toastError(error.message)
        }
        // You can display a message to the user or redirect them to the login page
        else if (error.response && error.response.status === 401) {
          // alert('Not Authenticated ')
          // window.location.href = '/login'
          toastError(error.message)
        } else {
          console.error('Error fetching expense data:', error)
          toastError(error.message)
        }
      } finally {
        setIsLoading(false) // End loading
      }
    }
    fetchExpense(expenseId)
  }, [])

  if (isLoading) {
    return (
      <div className='container '>
        <Loading />
      </div>
    )
  }

  if (expenseData.submitStatus === 'FALSE') {
    return (
      <div class='lc-block mb-5'>
        <h2>
          <span className='mark'>Not Found</span>
        </h2>
      </div>
    )
  }

  return (
    <div className='container mb-5 '>
      <div class='lc-block mb-5'>
        <h4>
          Expense Report <span className='mark'>{expenseId}</span>
        </h4>
      </div>
      <div className='row g-4'>
        <div className='col-md-6'>
          <div className='card'>
            {/* Details section */}
            <div className='card-body'>
              <div class='lc-block1'>
                <table class='table table-striped' align='center'>
                  <tbody>
                    <tr>
                      <th>Expense ID</th>
                      <td>{expenseData.id}</td>
                    </tr>
                    <tr>
                      <th>Expense Category</th>
                      <td>{expenseData.category}</td>
                    </tr>
                    <tr>
                      <th>Expense Date</th>
                      <td>{expenseData.expenseDate}</td>
                    </tr>
                    <tr>
                      <th>Expense Status</th>
                      <td>
                        {expenseData.status === 'REJECTED' ? (
                          <span className='bg-danger'>
                            {expenseData.status}
                          </span>
                        ) : expenseData.status === 'PENDING' ? (
                          <span className='bg-warning'>
                            {expenseData.status}
                          </span>
                        ) : (
                          <span className='bg-success'>
                            {expenseData.status}
                          </span>
                        )}
                      </td>
                    </tr>
                    <tr>
                      <th>Submit Status</th>
                      <td>
                        {expenseData.submitStatus === 'TRUE'
                          ? 'SUBMITTED'
                          : 'NOT SUBMITTED'}
                      </td>
                    </tr>
                    <tr>
                      <th>Expense Amount (Rs.)</th>
                      <td>{expenseData.amount}</td>
                    </tr>
                    <tr>
                      <th>Expense Description</th>
                      <td>
                        <textarea className='w-100' rows='4' disabled>
                          {expenseData.description}
                        </textarea>
                      </td>
                    </tr>

                    <tr>
                      <th>Manager Comment</th>
                      <td>
                        {expenseData.mgrComment == null
                          ? 'No comments'
                          : expenseData.mgrComment}
                      </td>
                    </tr>
                  </tbody>
                </table>

                <div>
                        <input
                          name=''
                          id=''
                          class='btn btn-success mx-3'
                          type='button'
                          value='APPROVE'
                          onClick={handleApproval}
                        />
                        <input
                          name=''
                          id=''
                          class='btn btn-danger mx-3'
                          type='button'
                          value='REJECT'
                          onClick={handleRejection}
                        />
                      </div>
              </div>
            </div>
          </div>
          <div>
            <form
              className='mt-3 d-flex flex-grow-1 align-items-center'
              onSubmit={handleAddComment}
            >
              <div className=' flex-grow-1 me-2'>
                <textarea
                  onChange={onChangeHandler}
                  placeholder='Enter your comment here'
                  className='w-100 form-control'
                ></textarea>
              </div>
              <button type='submit' className='btn btn-success'>
                Add Comment
              </button>
            </form>
          </div>
        </div>

        <div className='col-md-6'>
          <div className='card'>
            {/* Attachments section */}
            <div className='card-body'>
              <h4 className='card-title'>Attachments</h4>
              <div className='accordion' id='accordionExample'>
                {/* ... (existing attachments code) */}
              </div>
            </div>
          </div>
          <div className='card mt-3 h-75 '>
            {/* Receipts section */}
            <div className='card-body h-100'>
              {expenseData.receipt ? (
                expenseData.receipt.fileType === 'pdf' ? (
                  <iframe
                    src={`data:application/pdf;base64,${expenseData.receipt.receiptFile}`}
                    width='100%'
                    height='100%'
                    frameBorder='0'
                    title='Receipt PDF'
                  ></iframe>
                ) : (
                  <img
                    src={`data:image/jpeg;base64,${expenseData.receipt.receiptFile}`}
                    width='100%'
                    height='100%'
                    alt='Receipt'
                  />
                )
              ) : (
                <p className='lead my-5'>No Attachments Found</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ExpenseReportManager
