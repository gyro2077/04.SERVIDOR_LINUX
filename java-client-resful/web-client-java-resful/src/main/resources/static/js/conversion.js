const units = {
    'Mass': [
        { value: 'KILOGRAM', text: 'Kilogramo' },
        { value: 'GRAM', text: 'Gramo' },
        { value: 'POUND', text: 'Libra' },
        { value: 'OUNCE', text: 'Onza' }
    ],
    'Length': [
        { value: 'METER', text: 'Metro' },
        { value: 'KILOMETER', text: 'Kilometro' },
        { value: 'CENTIMETER', text: 'Centimetro' },
        { value: 'MILE', text: 'Milla' },
        { value: 'YARD', text: 'Yarda' },
        { value: 'FOOT', text: 'Pie' },
        { value: 'INCH', text: 'Pulgada' }
    ],
    'Temperature': [
        { value: 'CELSIUS', text: 'Celsius' },
        { value: 'FAHRENHEIT', text: 'Fahrenheit' },
        { value: 'KELVIN', text: 'Kelvin' }
    ]
};

function updateUnits(category, selectedFrom, selectedTo) {
    const fromSelect = document.getElementById('fromUnit');
    const toSelect = document.getElementById('toUnit');
    
    fromSelect.innerHTML = '';
    toSelect.innerHTML = '';
    
    const unitList = units[category] || [];
    
    unitList.forEach(unit => {
        const fromOption = document.createElement('option');
        fromOption.value = unit.value;
        fromOption.textContent = unit.text;
        fromSelect.appendChild(fromOption);
        
        const toOption = document.createElement('option');
        toOption.value = unit.value;
        toOption.textContent = unit.text;
        toSelect.appendChild(toOption);
    });
    
    if (selectedFrom) {
        fromSelect.value = selectedFrom;
    }
    if (selectedTo) {
        toSelect.value = selectedTo;
    }
}

document.getElementById('category').addEventListener('change', function() {
    updateUnits(this.value, null, null);
});