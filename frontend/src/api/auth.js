const API_BASE_URL = 'http://localhost:8080/api/auth';

/**
 * Registers a new user.
 * @param {Object} userData - The user details.
 * @param {string} userData.email - The user's email.
 * @param {string} userData.passwordHash - The user's plain text password.
 * @param {number} [userData.roleId=1] - The user's role ID (optional, defaults to 1).
 * @returns {Promise<Object>} The registered user data or throws an error.
 */
export const registerUser = async (userData) => {
    try {
        const response = await fetch(`${API_BASE_URL}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Registration failed');
        }

        return await response.json();
    } catch (error) {
        console.error('Error during registration:', error);
        throw error;
    }
};

/**
 * Logs in a user and requests an OTP.
 * @param {string} email - The user's email.
 * @param {string} password - The user's password.
 * @returns {Promise<Object>} A success message indicating OTP was sent or throws an error.
 */
export const loginUser = async (email, password) => {
    try {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Login failed');
        }

        return await response.json();
    } catch (error) {
        console.error('Error during login:', error);
        throw error;
    }
};

/**
 * Verifies the OTP and returns the JWT token.
 * @param {number} userId - The ID of the user.
 * @param {string} code - The OTP code sent to the email.
 * @returns {Promise<Object>} The JWT token and success message or throws an error.
 */
export const verifyOtp = async (userId, code) => {
    try {
        const response = await fetch(`${API_BASE_URL}/verify-otp`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ userId, code }),
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'OTP verification failed');
        }

        return await response.json();
    } catch (error) {
        console.error('Error verifying OTP:', error);
        throw error;
    }
};
