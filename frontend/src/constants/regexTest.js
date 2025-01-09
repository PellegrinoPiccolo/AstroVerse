const textRegex = /^[A-Za-zÀ-ÿ\s]{2,30}$/
const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/
const usernameRegex = /^[A-Za-z0-9._\-\s]{3,20}$/
const descriptionRegex = /^[\w\s!-/:-@[-`{-~]{1,200}$/
const titleRegex = /^[A-Za-zÀ-ù0-9,‘\-\s]{2,50}$/

export const isNotOrNull = (value) => value === '' || value === null
export const isValidText = (value) => !isNotOrNull(value) && textRegex.test(value)
export const isValidEmail = (value) => !isNotOrNull(value) && emailRegex.test(value);
export const isValidPassword = (value) => !isNotOrNull(value) && passwordRegex.test(value)
export const isValidUsername = (value) => !isNotOrNull(value) && usernameRegex.test(value)
export const isValidDescription = (value) => !isNotOrNull(value) && descriptionRegex.test(value)
export const isValidTitle = (value) => !isNotOrNull(value) && titleRegex.test(value)

export const isValidImageType = (file) => {
    const validTypes = ['image/jpeg', 'image/png'];
    const maxSizeBytes = 200 * 1024 * 1024
    return validTypes.includes(file.type) && file.size <= maxSizeBytes;
}