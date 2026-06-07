// State and DOM Elements
let isLoginMode = true;
let currentUser = null;
let currentName = null;
let currentRole = null;
let currentId = null;
let currentEntity = null;
let currentEditId = null;

const roleNames = {'ROLE_ADMIN':'Администратор','ROLE_DOCTOR':'Лекар','ROLE_PATIENT':'Пациент'};
const colLabels = {'name':'Име','uin':'УИН','specialty':'Специалност','generalPractitioner':'Личен Лекар','egn':'ЕГН','insured':'Осигурен','description':'Описание','date':'Дата','treatment':'Лечение','additionalInfo':'Доп. Инфо','price':'Цена','paidByNzok':'НЗОК','startDate':'Начало','durationDays':'Дни','patient.name':'Пациент','doctor.name':'Лекар','diagnosis.name':'Диагноза','healthy':'Здрав'};
const entityLabels = {'doctors':'Лекари','patients':'Пациенти','diagnoses':'Диагнози','appointments':'Прегледи','sickLeaves':'Болнични'};

window.currentAppFilter = 'all';
window.currentAppSort = '';

const loginView = document.getElementById('loginView');
const dashboardView = document.getElementById('dashboardView');
const authForm = document.getElementById('authForm');
const logoutBtn = document.getElementById('logoutBtn');
const navMenu = document.getElementById('navMenu');
const mainContent = document.getElementById('mainContent');
const pageTitle = document.getElementById('pageTitle');

const tabLogin = document.getElementById('tabLogin');
const tabRegister = document.getElementById('tabRegister');
const formTitle = document.getElementById('formTitle');
const authSubmitBtn = document.getElementById('authSubmitBtn');
const registerFields = document.getElementById('registerFields');
const roleSelect = document.getElementById('roleSelect');
const patientFields = document.getElementById('patientFields');
const doctorFields = document.getElementById('doctorFields');
const loginError = document.getElementById('loginError');
const loginSuccess = document.getElementById('loginSuccess');

const formModal = document.getElementById('formModal');
const genericForm = document.getElementById('genericForm');
const modalFormFields = document.getElementById('modalFormFields');
const modalTitle = document.getElementById('modalTitle');
const closeModalBtn = document.getElementById('closeModalBtn');
