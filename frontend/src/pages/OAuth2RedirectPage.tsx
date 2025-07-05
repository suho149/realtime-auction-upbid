import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

// 백엔드 API와 통신할 axios 인스턴스 생성
// baseURL과 withCredentials 옵션이 매우 중요합니다.
const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true, // 모든 요청에 쿠키를 포함시키도록 설정
});

export default function OAuth2RedirectPage() {
    const navigate = useNavigate();

    useEffect(() => {
        // 이 컴포넌트가 렌더링되면 즉시 토큰 재발급 함수를 실행합니다.
        const reissueToken = async () => {
            try {
                console.log("토큰 재발급을 시도합니다...");

                // 백엔드의 토큰 재발급 API(/api/auth/reissue)를 호출합니다.
                // 이때 브라우저는 백엔드에서 받은 refresh_token 쿠키를 자동으로 요청 헤더에 실어 보냅니다.
                const response = await apiClient.post('/api/auth/reissue');

                // 서버로부터 응답받은 데이터에서 accessToken을 추출합니다.
                const { accessToken } = response.data;

                if (accessToken) {
                    console.log("새로운 Access Token을 성공적으로 받았습니다.");
                    // 새로운 Access Token을 LocalStorage에 저장합니다.
                    // (Refresh Token은 HttpOnly 쿠키에 안전하게 보관되어 있으므로 여기서는 신경쓰지 않아도 됩니다.)
                    localStorage.setItem('accessToken', accessToken);

                    // 모든 과정이 성공했으므로, 메인 페이지로 이동합니다.
                    navigate('/');
                } else {
                    // 응답은 성공(200 OK)했지만 accessToken이 없는 비정상적인 경우
                    throw new Error("Access Token이 응답에 포함되지 않았습니다.");
                }
            } catch (error) {
                // API 호출 과정에서 에러가 발생한 경우 (예: 401 Unauthorized)
                console.error("토큰 재발급에 실패했습니다:", error);
                alert('로그인에 실패하였습니다. 다시 시도해주세요.');
                navigate('/login'); // 실패 시 로그인 페이지로 돌려보냅니다.
            }
        };

        reissueToken();

        // useEffect의 의존성 배열에 navigate를 추가하여, navigate 함수가 변경될 때를 대비합니다.
    }, [navigate]);

    return <div>로그인 처리 중입니다. 잠시만 기다려주세요...</div>;
}