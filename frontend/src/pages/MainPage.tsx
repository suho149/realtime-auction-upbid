export default function MainPage() {
    const token = localStorage.getItem('accessToken');
    return (
        <div>
            <h1>메인 페이지</h1>
            {token ? <p>로그인 성공! 토큰: {token}</p> : <p>로그인이 필요합니다.</p>}
        </div>
    )
}