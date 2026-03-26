import http from 'k6/http';
import { check, fail, sleep } from 'k6';

export const options = {
  scenarios: {
    cached_get_by_id_reads: {
      executor: 'constant-vus',
      vus: 25,
      duration: '2m',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<200'],
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

  return { token: loginRes.json('accessToken') };
}

export default function (data) {
  const headers = {
    Authorization: `Bearer ${data.token}`,
    Accept: 'application/json',
  };

  const bookId = __ENV.BOOK_ID || '1';
  const authorId = __ENV.AUTHOR_ID || '1';

  const responses = http.batch([
    ['GET', `${BASE_URL}/books/${bookId}`, null, { headers }],
    ['GET', `${BASE_URL}/authors/${authorId}`, null, { headers }],
  ]);

  check(responses[0], {
    'GET /books/{id} is 200': (r) => r.status === 200,
    'GET /books/{id} has id': (r) => {
      try {
        return String(r.json('id')) === String(bookId);
      } catch (_) {
        return false;
      }
    },
  });

  check(responses[1], {
    'GET /authors/{id} is 200': (r) => r.status === 200,
    'GET /authors/{id} has id': (r) => {
      try {
        return String(r.json('id')) === String(authorId);
      } catch (_) {
        return false;
      }
    },
  });

  sleep(0.2);
}