package com.example.rideroffercomparison.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }

    val isHindi = currentLanguage == "hi"

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Language Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = if (isHindi) "English" else "हिंदी", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isHindi,
                    onCheckedChange = { onLanguageChange(if (it) "hi" else "en") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isHindi) "राइडर ऑफर तुलना" else "Rider Offer Comparison",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isHindi) "स्वतंत्र कमाई अनुमानक और ऑफर विश्लेषक। किसी भी प्लेटफॉर्म से संबद्ध नहीं।"
                else "Independent earnings estimator & offer analyzer. Not affiliated with any platform.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (!otpSent) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text(if (isHindi) "फ़ोन नंबर" else "Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { if (phoneNumber.length >= 10) otpSent = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = phoneNumber.isNotBlank()
                ) {
                    Text(if (isHindi) "OTP प्राप्त करें" else "Get OTP")
                }
            } else {
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { otpCode = it },
                    label = { Text(if (isHindi) "OTP दर्ज करें (उदा. 1234)" else "Enter OTP (e.g. 1234)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onLoginSuccess(phoneNumber) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = otpCode.isNotBlank()
                ) {
                    Text(if (isHindi) "सत्यापित करें और आगे बढ़ें" else "Verify & Continue")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "🔒 गोपनीयता और डेटा सुरक्षा" else "🔒 Privacy & Data Safeguards",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHindi) "• ऑफर डेटा कड़ाई से आपके फोन पर रहता है。\n• किसी भी ग्राहक का नाम, फोन या पता कभी नहीं पढ़ा जाता है。\n• ऑफर कार्ड पढ़ने के लिए एक्सेसिबिलिटी अनुमति की आवश्यकता होती है।"
                        else "• Offer data stays strictly on your phone.\n• Zero customer names, phones, or addresses are ever read.\n• Requires Accessibility permission to read offer cards.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
