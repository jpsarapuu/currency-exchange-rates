class CurrencyList {

    currencies = {};

    // For confirmation prompt
    deletableId;

    addCurrency(currency) {
        this.currencies[currency.id] = currency;
    }

    removeCurrency(currency) {
        delete this.currencies[currency.id];
    }

    updateCurrency(currency) {
        this.currencies[currency.id] = currency;
    }

    refreshUI() {

        // Clear UI except for add button
        while (tbody.firstElementChild !== addTr) {
            tbody.firstElementChild.remove();
        }

        const _this = this;

        // Create tr for every currency
        Object.keys(this.currencies).forEach(function (key) {

            const tr = document.createElement('tr');
            tr.id = _this.currencies[key].id;

            // Create tds based on field names and order from back-end
            for (let i = 0; i < propertiesInfo.length; i++) {
                const td = document.createElement('td');
                td.appendChild(document.createTextNode(_this.currencies[key][propertiesInfo[i]]));
                tr.appendChild(td);
            }

            // Retrieve affordances
            let affordedMethods = [];
            if (_this.currencies[key]._templates !== undefined) {
                Object.keys(_this.currencies[key]._templates).forEach(function (template) {
                    affordedMethods.push(_this.currencies[key]._templates[template].method);
                });
            }

            // Add update button if in affordances
            if (affordedMethods.includes('put')) {
                const updateTd = document.createElement('td');
                const updateBtn = document.createElement('button');
                updateBtn.className = 'btn-sm btn-primary update';
                updateBtn.appendChild(document.createTextNode('Update'));
                updateTd.appendChild(updateBtn);
                tr.appendChild(updateTd);
            } else {
                tr.appendChild(document.createElement('td'));
            }

            // Add delete button if in affordances
            if (affordedMethods.includes('delete')) {
                const deleteTd = document.createElement('td');
                const deleteBtn = document.createElement('button');
                deleteBtn.className = 'btn-sm btn-secondary delete';
                deleteBtn.appendChild(document.createTextNode('Delete'));
                deleteTd.appendChild(deleteBtn);
                tr.appendChild(deleteTd);
            } else {
                tr.appendChild(document.createElement('td'));
            }

            tbody.insertBefore(tr, addTr);
        });
    }
}

const currencyList = new CurrencyList();

// UI elements
const container = document.getElementById('container');
const theadTr = document.getElementById('thead-tr');
const tbody = document.getElementById('tbody');
const addTr = document.getElementById('add-tr');
const promptBtn = document.getElementById('prompt-btn');

// getCurrencies() response
let currenciesResp;

// HATEOAS instructions
let propertiesInfo;

// Initial request to all currencies
const curReq = new XMLHttpRequest();
curReq.open('GET', 'http://localhost:8080/currencies');
curReq.onload = function () {
    currenciesResp = JSON.parse(this.responseText);

    // Request field names and order for currencies
    const infoReq = new XMLHttpRequest();
    infoReq.open('GET', currenciesResp._links.propertiesInfo.href);
    infoReq.onload = function () {
        propertiesInfo = JSON.parse(this.responseText);
        initUI();
    };
    infoReq.send();
};
curReq.send();

setTimeout(function () {
if (currenciesResp === undefined) {
    feedback('Unable to connect to the server.');
}
}, 5000);

// Event listener for update and delete buttons
tbody.addEventListener('click', function (e) {

    if (e.target.classList.contains('update')) {

        const updateBtn = e.target;
        const tr = updateBtn.parentElement.parentElement;

        if (updateBtn.textContent === 'Update') {

            // Turn all tds representing currency fields in tr to input fields
            for (let i = 0; i < propertiesInfo.length; i++) {
                const td = tr.children[i];
                const tdValue = td.textContent;
                td.removeChild(td.firstChild);

                const input = document.createElement('input');
                input.className = 'form-control';
                input.type = 'text';
                input.value = tdValue;

                td.appendChild(input);
            }

            updateBtn.textContent = 'Save';

        } else if (updateBtn.textContent === 'Save') {

            const currency = {};

            // Gather data from input fields to currency object
            for (let i = 0; i < propertiesInfo.length; i++) {
                currency[propertiesInfo[i]] = tr.children[i].firstElementChild.value;
            }

            // Send currency to the server to be updated
            const putReq = new XMLHttpRequest();
            putReq.open('PUT', currencyList.currencies[tr.id]._links.self.href);
            putReq.setRequestHeader('content-type', 'application/json');
            putReq.onload = function () {

                const response = JSON.parse(this.responseText);
                const responseMessage = extractResponseMessage(response);

                if (this.status === 200) {
                    feedback(`${responseMessage} successfully updated.`, 'success');
                    currencyList.updateCurrency(response);
                } else {
                    if (responseMessage === undefined) {
                        feedback('Unable to update currency: Bad request.');
                    } else {
                        feedback(responseMessage);
                    }
                }

                // Turn tds with input elements back to tds with currency fields
                // If request was successful, also updating the currency
                currencyList.refreshUI();
            };
            putReq.send(JSON.stringify(currency));
        }
    }

    if (e.target.classList.contains('delete')) {

        const deleteBtn = e.target;
        const tr = deleteBtn.parentElement.parentElement;

        // Store the id of target currency for future deletion if user confirms the request on prompt modal
        currencyList.deletableId = tr.id;

        $('#prompt').modal('show');
    }
});

// Delete currency if user clicks Yes on the prompt:
promptBtn.addEventListener('click', function () {

    const delReq = new XMLHttpRequest();
    delReq.open('DELETE', currencyList.currencies[currencyList.deletableId]._links.self.href);
    delReq.onload = function () {
        if (this.status === 204) {
            feedback(currencyList.currencies[currencyList.deletableId][propertiesInfo[0]] +
                ' successfully deleted.', 'success');

            currencyList.removeCurrency(currencyList.currencies[currencyList.deletableId]);
            currencyList.refreshUI();
        }
    };
    delReq.send();
});

// For adding new elements
addTr.addEventListener('click', function (e) {

    if (e.target.classList.contains('add')) {

        const tr = document.createElement('tr');

        // Create input fields in tr
        for (let i = 0; i < propertiesInfo.length; i++) {
            const td = document.createElement('td');
            const input = document.createElement('input');
            input.className = 'form-control';
            input.placeholder = capitalize(propertiesInfo[i]);
            td.appendChild(input);
            tr.appendChild(td);
        }

        // Add submit button
        const submitTd = document.createElement('td');
        const submitBtn = document.createElement('button');
        submitBtn.className = 'btn-sm btn-primary submit';
        submitBtn.appendChild(document.createTextNode('Submit'));
        submitTd.appendChild(submitBtn);
        tr.appendChild(submitTd);

        // Add cancel button
        const cancelTd = document.createElement('td');
        const cancelBtn = document.createElement('button');
        cancelBtn.className = 'btn-sm btn-dark cancel';
        cancelBtn.appendChild(document.createTextNode('Cancel'));
        cancelTd.appendChild(cancelBtn);
        tr.appendChild(cancelTd);

        // Append tr to tbody
        tbody.appendChild(tr);

        // Hide the add button while currency creation in process
        addTr.style.display = 'none';

        submitBtn.addEventListener('click', function () {

            const currency = {};

            // Gather data from input fields to currency object
            for (let i = 0; i < propertiesInfo.length; i++) {
                currency[propertiesInfo[i]] = tr.children[i].firstElementChild.value;
            }

            // Send currency to the server to be created
            const postReq = new XMLHttpRequest();
            postReq.open('POST', currenciesResp._links.self.href);
            postReq.setRequestHeader('content-type', 'application/json');
            postReq.onload = function () {

                const response = JSON.parse(this.responseText);
                const responseMessage = extractResponseMessage(response);

                if (this.status === 201) {
                    feedback(`${responseMessage} successfully added.`, 'success');
                    tr.remove();

                    // If request is successful, remove the tr for input and restore the add button
                    addTr.style.display = '';
                    currencyList.addCurrency(response);
                    currencyList.refreshUI();
                } else {
                    if (responseMessage === undefined) {
                        feedback("Unable to add currency: Bad request.");
                    } else {
                        feedback(responseMessage);
                    }
                }
            };
            postReq.send(JSON.stringify(currency));
        });

        // Cancel currency creation
        cancelBtn.addEventListener('click', function () {
            tr.remove();
            addTr.style.display = '';
        })
    }
});

// Initial UI creation
function initUI() {

    // Adjust table's column width to fit all the fields
    const columnWidth = (80 / propertiesInfo.length) + '%';

    // Create th for every field
    propertiesInfo.forEach(function (property) {
        const th = document.createElement('th');
        th.style.width = columnWidth;
        th.appendChild(document.createTextNode(capitalize(property)));
        theadTr.appendChild(th);
    });

    // Leave space in table for potential update and delete buttons
    for (let i = 0; i < 2; i++) {
        const emptyTh = document.createElement('th');
        emptyTh.style.width = '10%';
        theadTr.append(emptyTh);
    }

    // Retrieve currencies from the getCurrencies() response if any
    if (currenciesResp._embedded !== undefined) {
        currenciesResp._embedded.currencies.forEach(function (currency) {
            currencyList.addCurrency(currency);
        });
    }

    // Retrieve affordances
    let affordedMethods = [];

    if (currenciesResp._templates !== undefined) {
        Object.keys(currenciesResp._templates).forEach(function (template) {
            affordedMethods.push(currenciesResp._templates[template].method);
        });
    }

    // Enable the add button if afforded
    if (affordedMethods.includes('post')) {
        // Create enough empty tds for add button for clean visuals
        for (let i = 0; i <= propertiesInfo.length; i++) {
            addTr.appendChild(document.createElement('td'))
        }
        addTr.style.display = '';
    }

    currencyList.refreshUI();
}

// Searches the response body for a non-empty string field which could either represent a currency
// or carry an error message
function extractResponseMessage(response) {

    let responseMessage = undefined;
    for (let i = 0; i < propertiesInfo.length; i++) {
        if (typeof response[propertiesInfo[i]] === 'string' && response[propertiesInfo[i]] !== '') {
            responseMessage = response[propertiesInfo[i]];
            break;
        }
    }
    return responseMessage;
}

// For alerts
function feedback(message, type = 'danger') {

    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.appendChild(document.createTextNode(message));

    container.appendChild(alert);

    setTimeout(function () {
        container.removeChild(alert);
    }, 5000);
}

function capitalize(string) {
    return typeof string === 'string' ? string.charAt(0).toUpperCase() + string.slice(1) : string;
}