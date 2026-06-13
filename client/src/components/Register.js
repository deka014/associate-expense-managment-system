import React, { useEffect } from 'react'
import { useState } from 'react'

import axios from 'axios'
import authHeader from '../services/authHeader'
import { toastError } from '../utils/notification'
//more than 1 len , 1 specail char , 1 letter , 5 or more
const passwordPattern = /^(?=.*\d)(?=.*[!@#$%^&*])(?=.*[a-zA-Z]).{5,}$/

const Register = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    phone: '',
    jobTitle: '',
    department: '',
    role: '',
    managerId: '',
  })

  function handleOnChange(event) {
    const { name, value } = event.target
    setFormData((prevState) => ({
      ...prevState,
      [name]: value,
    }))
  }

  const handleOnRegister = async (e) => {
    e.preventDefault()
    console.log("running")
    // Check if password meets the criteria
    console.log(!passwordPattern.test(formData.password),"saras")
    if (!passwordPattern.test(formData.password)) {
      alert(
        'Password must contain at least one number, one special symbol, and one character.'
      )
      return // Prevent form submission
    }

    axios
      .post('http://localhost/auth/register', formData, {
        headers: authHeader(),
      })
      .then((response) => {
        alert('User created successfully.')
        window.location.href = `/login`
        // Handle successful response
      })
      .catch((error) => {
        console.log(error)
        toastError(error.message)
        // window.location.href = `/hr-reports`
      })
  }

  return (
    <div className='wrapper bg-dark w-50 mb-5'>
      <h2>Sign Up</h2>
      <form>
        <div className='input-box'>
          <input
            type='text'
            name='name'
            class='form-control'
            id='name'
            placeholder='Name'
            required
            value={formData.name}
            onChange={handleOnChange}
          />
        </div>

        <div className='input-box'>
          <input
            type='email'
            name='email'
            class='form-control'
            id='email'
            placeholder='Email'
            required
            value={formData.email}
            onChange={handleOnChange}
          />
        </div>

        <div className='input-box'>
          <input
            type='tel'
            name='phone'
            class='form-control'
            id='phone'
            placeholder='Phone'
            required
            value={formData.phone}
            onChange={handleOnChange}
          />
        </div>

        <div className='input-box'>
          <input
            type='text'
            name='jobTitle'
            class='form-control'
            id='jobTitle'
            placeholder='Job Title'
            value={formData.jobTitle}
            onChange={handleOnChange}
          />
        </div>

        <div className='input-box'>
          <input
            type='text'
            name='department'
            class='form-control'
            id='department'
            placeholder='Department'
            value={formData.department}
            onChange={handleOnChange}
          />
        </div>

        <div className='input-box'>
          <input
            type='password'
            name='password'
            class='form-control'
            id='pass'
            placeholder='Password'
            required
            value={formData.password}
            onChange={handleOnChange}
          />
        </div>

        <div className='input-box'>
          <input
            type='text'
            name='role'
            class='form-control'
            id='role_id'
            placeholder='Role'
            required
            value={formData.role}
            onChange={handleOnChange}
          />
        </div>

        <div className='input-box'>
          <input
            type='text'
            name='managerId'
            class='form-control'
            id='managerId'
            placeholder='Manager ID'
            required
            value={formData.managerId}
            onChange={handleOnChange}
          />
        </div>

        <button class='w-50' type='submit' onClick={handleOnRegister}>
          Add Employee
        </button>
      </form>
    </div>
  )
}

export default Register
