export default function LoginPage() {
    const googleLoginUrl = 'http://localhost:8080/oauth2/authorization/google';
    const kakaoLoginUrl = 'http://localhost:8080/oauth2/authorization/kakao';

    return (
        <div>
            <h1>소셜 로그인</h1>
            <a href={googleLoginUrl}>
                <button>구글 로그인</button>
            </a>
            <a href={kakaoLoginUrl}>
                <button>카카오 로그인</button>
            </a>
        </div>
    );
};