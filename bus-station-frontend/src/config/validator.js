const usernameMinLength = 5;
const emailMinLength = 5;
const passwordMinLength = 3;

const validateUsername = (username) => {
  if (
    username === null ||
    username === '' ||
    username.length < usernameMinLength
  )
    return `Tên tài khoản dài ít nhất ${usernameMinLength} ký tự`;

  return null;
};

const validateEmail = (email) => {
  if (email === null || email === '' || email.length < emailMinLength)
    return `Email dài  ít nhất ${emailMinLength} ký tự`;
  if (
    !email
      .toLowerCase()
      .match(
        /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|.(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
      )
  )
    return `Email không đúng`;
  return null;
};

const validatePassword = (password) => {
  if (
    password === null ||
    password === '' ||
    password.length < passwordMinLength
  )
    return `Mật khẩu dài ít nhất ${password} ký tự`;

  return null;
};

export {validateEmail, validateUsername, validatePassword};
