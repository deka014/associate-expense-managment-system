import React, { useState, useEffect } from 'react'
import axios from 'axios'
import { useParams } from 'react-router-dom'
import Loading from './Loading'
import authHeader from '../services/authHeader'
import { toastError, toastSuccess } from '../utils/notification'
import { toast } from 'react-toastify'

const ExpenseReport = () => {
  const [expenseData, setExpenseData] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [receiptFileType, setReceiptFileType] = useState('')

  const { expenseId, userId } = useParams()

  const [amount, setAmount] = useState('')
  const [description, setDescription] = useState('')

  const [receiptData, setReceiptData] = useState({
    receiptFile: null,
  })

  const handleOnFileChange = (event) => {
    event.preventDefault()

    if (event.target.name === 'receiptFile' && event.target.files[0]) {
      const file = event.target.files[0];
      const allowedTypes = ['application/pdf', 'image/jpeg', 'image/png']

      if (!allowedTypes.includes(file.type)) {
        alert('Invalid file type. Only PDF, JPEG, and PNG files are allowed.')
        return
      }
      setReceiptData({
        [event.target.name]: file,
      })
    }
  }

  const handleUpdateRecipt = (event) => {
    event.preventDefault()
    const formData = new FormData()
    console.log('rinn')
    formData.append('file', receiptData.receiptFile)
    console.log()
    axios
      .put(
        `http://localhost/expense/save/${expenseId}/${expenseData.receipt.id}`,
        formData,
        {
          headers: authHeader(),
        }
      )
      .then((response) => {
        console.log(response.data)
        window.location.href = `/expense-report/user/${userId}/${expenseId}`
      })
      .catch((error) => {
        console.log(error)
        toastError('No Receipt Found')
      })
  }

  const handleAddRecipt = (event) => {
    event.preventDefault()
    const formData = new FormData()
    formData.append('file', receiptData.receiptFile)
    console.log(formData)
    console.log('handle add recipt')
    axios
      .post(`http://localhost/expense/save/${expenseId}`, formData, {
        headers: authHeader(),
      })
      .then((response) => {
        console.log(response.data)
        window.location.href = `/expense-report/user/${userId}/${expenseId}`
      })
      .catch((error) => {
        console.log(error)
        toastError('No Receipt Found')
      })
  }

  const handleDelete = (event) => {
    event.preventDefault()
    if (window.confirm("Are you sure you want to delete this expense?")) {
    axios
      .delete(`http://localhost/expense/delete/${expenseId}`, {
        headers: authHeader(),
      })
      .then((response) => {
        const exp_Id = response.data.id
        window.location.href = `/expense/${userId}`
      })
      .catch((error) => {
        console.error('Error updating expense:', error)
        toastError(error)
        // Handle error
      })
    }
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    console.log('Clicked on Submit')
    console.log(expenseId)

    try {
      // Attempt to update the expense
      await handleUpdate()

      // If update is successful, proceed with submission
      await axios.put(
        `http://localhost/expense/submit/${expenseId}`,
        {},
        { headers: authHeader() }
      )
      window.location.reload()
    } catch (error) {
      console.error('Error during update or submission:', error)
      toastError(error)
      // Handle error appropriately
    }
  }

  const handleUpdate = async () => {
    console.log('Clicked on Update!')

    try {
      await axios.put(
        `http://localhost/expense/update-expense/${expenseId}?description=${description}&amount=${amount}`,
        {},
        { headers: authHeader() }
      )
      toastSuccess('Expense Successfully Updated')
      //  alert(`Expense with id: ${expenseId} updated successfully`);
    } catch (error) {
      console.error('Error updating expense:', error)
      toastError(error.message)
      // Handle error appropriately
      // Rethrow the error to be caught by the calling function
    }
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
        console.log('EXPENSE DESCRIPTION ---->', expenseData.description)
        setDescription(response.data.description)
        setAmount(response.data.amount)

        console.log(' DESCRIPTION STATE ---->', description)
        console.log('AMOUNT STATE ---->', amount)
      } catch (error) {
        // if (error.response && error.response.status === 403) {
        //   // Handle 403 Unauthorized error
        //   window.location.href = '/'
        // }
        // // You can display a message to the user or redirect them to the login page
        // else if (error.response && error.response.status === 401) {
        //   // alert('Not Authenticated ')
        //   // window.location.href = '/login'
        // } else {
        console.error('Error fetching expense data:', error)
        window.location.href = '/login'
        // }
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

  return (
    //use bootstrap to create a card with the above details
    <div>
      <div class='row g-2 overflow-hidden'>
        <div class='col-md-6'>
          <div class='lc-block mb-5'>
            <h4>
              {/* Employee Details for <span className='mark'>{employee.name}</span> */}{' '}
              Expense Details
            </h4>
          </div>
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
                    {' '}
                    {expenseData.status === 'REJECTED' ? (
                      <span className='bg-danger'>{expenseData.status}</span>
                    ) : expenseData.status === 'PENDING' ? (
                      <span className='bg-warning'>{expenseData.status}</span>
                    ) : (
                      <span className='bg-success'>{expenseData.status}</span>
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
                  <td>
                    {expenseData.status === 'PENDING' ? (
                      <>
                        <input
                          type='number'
                          class='form-control'
                          value={amount}
                          onChange={(e) => setAmount(parseInt(e.target.value))}
                        />
                      </>
                    ) : (
                      expenseData.amount
                    )}
                  </td>
                </tr>
                <tr>
                  <th>Expense Description</th>
                  <td>
                    {expenseData.status === 'PENDING' ? (
                      <textarea
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        className='w-100'
                        rows='4'
                      ></textarea>
                    ) : (
                      <textarea className='w-100' rows='4' disabled>
                        {expenseData.description}
                      </textarea>
                    )}
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
            <div class='d-flex gap-2 justify-content-around mb-5'>
              {/* update button below */}
              {expenseData.status === 'PENDING' ? (
                <a
                  name=''
                  id=''
                  class='btn btn-warning'
                  role='button'
                  onClick={handleUpdate}
                >
                  Update
                </a>
              ) : (
                ''
              )}
              {/* submit button below */}
              {expenseData.submitStatus === 'FALSE' ? (
                <a
                  name=''
                  id=''
                  class='btn btn-success'
                  role='button'
                  onClick={handleSubmit}
                >
                  Submit
                </a>
              ) : (
                <button
                  type='button'
                  name=''
                  id=''
                  class='btn btn-primary'
                  disabled
                >
                  Already Submitted
                </button>
              )}
              {/* delete button */}
              <input
                name=''
                id=''
                class='btn btn-danger'
                type='button'
                value='Delete'
                onClick={handleDelete}
              />
            </div>
          </div>
        </div>
        <div class='col-md-6'>
          <div class='lc-block mb-5'>
            <h4>
              {/* Employee Details for <span className='mark'>{employee.name}</span> */}{' '}
              Attachments
            </h4>
          </div>
          <div class='accordion' id='accordionExample'>
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
                  Recipts Attached
                </button>
              </h2>
              <div
                id='collapseOne'
                class='accordion-collapse collapse show'
                aria-labelledby='headingOne'
                data-bs-parent='#accordionExample'
              >
                {/* <div class='accordion-body'>
                    This is the first item's accordion body.
                  </div> */}
              </div>
            </div>
          </div>
          <div class='lc-block1 h-100'>
            {expenseData.receipt ? (
              expenseData.receipt.fileType === 'pdf' ? (
                <iframe
                  src={`data:application/pdf;base64,${expenseData.receipt.receiptFile}`}
                  width='100%'
                  height='50%'
                  frameBorder='0'
                ></iframe>
              ) : (
                <img
                  src={`data:application/jpeg;base64,${expenseData.receipt.receiptFile}`}
                  width='100%'
                  height='50%'
                  alt='Receipt' // Add alt text for accessibility
                />
              )
            ) : (
              // <strong className='text-center'>File Not Found</strong>
              <p class='lead my-5'>No Attachments Found</p>
            )}
            <div>
              {' '}
              {/* {expenseData.submitStatus == 'FALSE' ? ( */}
              {expenseData.status == 'PENDING' ? (
                <div>
                  {' '}
                  <input
                    type='file'
                    name='receiptFile'
                    class='form-control my-4'
                    id='receiptFile'
                    onChange={handleOnFileChange}
                  />{' '}
                  {expenseData.receipt ? (
                    <button
                      type='button'
                      name=''
                      id=''
                      class='btn btn-warning my-4'
                      onClick={handleUpdateRecipt}
                    >
                      {' '}
                      Update Recipt{' '}
                    </button>
                  ) : (
                    <button
                      type='button'
                      name=''
                      id=''
                      class='btn btn-primary my-4'
                      onClick={handleAddRecipt}
                    >
                      Add New Receipt
                    </button>
                  )}{' '}
                </div>
              ) : (
                <button
                  type='button'
                  name=''
                  id=''
                  class='btn btn-outline-dark my-4'
                >
                  {expenseData.status === 'REJECTED' ? (
                    <span className='bg-danger'>{expenseData.status}</span>
                  ) : expenseData.status === 'PENDING' ? (
                    <span className='bg-warning'>{expenseData.status}</span>
                  ) : (
                    <span className='bg-success'>{expenseData.status}</span>
                  )}
                </button>
              )}{' '}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ExpenseReport
