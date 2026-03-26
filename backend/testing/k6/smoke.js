import http from 'k6/http';
import { check, fail } from 'k6';

export const options = {
  vus: 1,
  iterations: 1,
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<1000'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://app:8080/api';
const USERNAME = __ENV.USERNAME || 'admin';
const PASSWORD = __ENV.PASSWORD || 'admin123';

export default function () {
  const healthRes = http.get(`${BASE_URL}/actuator/health`, {
    headers: { Accept: 'application/json' },
  });

  check(healthRes, {
    'health status is 200': (r) => r.status === 200,
  });

  const loginRes = http.post(
    `${BASE_URL}/login`,
    JSON.stringify({
      username: USERNAME,
      password: PASSWORD,
    }),
    {
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
    }
  );

  const loginOk = check(loginRes, {
    'login status is 200': (r) => r.status === 200,
    'login returns token': (r) => {
      try {
        return !!r.json('accessToken');
      } catch (_) {
        return false;
      }
    },
  });

  if (!loginOk) {
    fail(`Login failed. Status=${loginRes.status}, body=${loginRes.body}`);
  }

  const token = loginRes.json('accessToken');

  const bookRes = http.get(`${BASE_URL}/books/1`, {
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: 'application/json',
    },
  });

  check(bookRes, {
    'book by id status is 200': (r) => r.status === 200,
  });
}