import http from 'k6/http';
import { check, sleep, fail } from 'k6';

export const options = {
  scenarios: {
    authenticated_reads: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 5 },
        { duration: '1m', target: 20 },
        { duration: '30s', target: 0 },
      ],
      gracefulRampDown: '10s',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
    checks: ['rate>0.99'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://app:8080/api';
const USERNAME = __ENV.USERNAME || 'admin';
const PASSWORD = __ENV.PASSWORD || 'admin123';

export function setup() {
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
    'setup login status is 200': (r) => r.status === 200,
    'setup login returns token': (r) => {
      try {
        return !!r.json('accessToken');
      } catch (_) {
        return false;
      }
    },
  });

  if (!loginOk) {
    fail(`Setup login failed. Status=${loginRes.status}, body=${loginRes.body}`);
  }

  return {
    token: loginRes.json('accessToken'),
  };
}

export default function (data) {
  const authHeaders = {
    Authorization: `Bearer ${data.token}`,
    Accept: 'application/json',
  };

  const responses = http.batch([
    ['GET', `${BASE_URL}/books/1`, null, { headers: authHeaders }],
    ['GET', `${BASE_URL}/books/2`, null, { headers: authHeaders }],
    ['GET', `${BASE_URL}/authors/1`, null, { headers: authHeaders }],
    ['GET', `${BASE_URL}/books?page=0&size=10&sort=id,asc`, null, { headers: authHeaders }],
  ]);

  check(responses[0], {
    'GET /books/1 is 200': (r) => r.status === 200,
  });

  check(responses[1], {
    'GET /books/2 is 200': (r) => r.status === 200,
  });

  check(responses[2], {
    'GET /authors/1 is 200': (r) => r.status === 200,
  });

  check(responses[3], {
    'GET /books page is 200': (r) => r.status === 200,
  });

  sleep(1);
}